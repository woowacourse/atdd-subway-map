package wooteco.subway.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.ui.dto.LineCreateRequest;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.SectionRequest;

@Service
@Transactional
public class LineService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.\n-> {name : %s}";
    private static final String NONE_LINE_ERROR_MESSAGE = "해당 ID의 노선은 존재하지 않습니다.\n-> {id : %d}";
    private static final String NONE_SECTION_ERROR_MESSAGE = "존재하지 않는 역입니다.\n-> {id : %d}";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse save(LineCreateRequest line) {
        validDuplicatedName(line.getName());
        validStations(line.getDownStationId(), line.getUpStationId());

        Long lineId = lineDao.save(line);

        SectionRequest sectionRequest = SectionRequest.from(line);
        sectionDao.save(sectionRequest.toEntity(lineId));

        List<StationResponse> stations = generateStationResponses(line.getDownStationId(), line.getUpStationId());
        return new LineResponse(lineId, line.getName(), line.getColor(), stations);
    }

    private void validDuplicatedName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException(String.format(DUPLICATED_NAME_ERROR_MESSAGE, name));
        }
    }

    private void validStations(Long... ids) {
        for (Long id : ids) {
            validStation(id);
        }
    }

    private void validStation(Long id) {
        if (!stationDao.existsById(id)) {
            throw new IllegalArgumentException(String.format(NONE_SECTION_ERROR_MESSAGE, id));
        }
    }

    private List<StationResponse> generateStationResponses(Long... ids) {
        return Arrays.stream(ids)
                .map(id -> StationResponse.from(stationDao.findById(id)))
                .collect(Collectors.toUnmodifiableList());
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedNameWithoutId(lineRequest.getName(), id);
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedNameWithoutId(String name, Long id) {
        if (lineDao.existsByNameExceptWithId(name, id)) {
            throw new IllegalArgumentException(String.format(DUPLICATED_NAME_ERROR_MESSAGE, name));
        }
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        if (line.isEmpty()) {
            throw new IllegalArgumentException(String.format(NONE_LINE_ERROR_MESSAGE, id));
        }
        Sections sections = new Sections(sectionDao.findByLineId(id));
        return LineResponse.from(line.get(), findStations(sections, id));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        Map<Long, List<Section>> sectionsMap = initSectionsMap(sectionDao.findAll());

        return lineDao.findAll()
                .stream()
                .map(line -> {
                    Long lineId = line.getId();
                    Sections sections = new Sections(sectionsMap.get(lineId));
                    return LineResponse.from(line, findStations(sections, lineId));
                })
                .collect(Collectors.toList());
    }

    private Map<Long, List<Section>> initSectionsMap(List<Section> sections) {
        Map<Long, List<Section>> map = new HashMap<>();
        for (Section section : sections) {
            Long lineId = section.getLineId();
            List<Section> lineSections = map.getOrDefault(lineId, new LinkedList<>());
            lineSections.add(section);
            map.put(lineId, lineSections);
        }
        return map;
    }

    private List<StationResponse> findStations(Sections sections, Long id) {
        List<Long> ids = sections.getSortedStationIds();
        Map<Long, String> nameInfo = initNameMap(id);

        return ids.stream()
                .map(it -> new StationResponse(it, nameInfo.get(it)))
                .collect(Collectors.toList());
    }

    private Map<Long, String> initNameMap(Long id) {
        return stationDao.findByLineId(id)
                .stream()
                .collect(Collectors.toMap(Station::getId, Station::getName));
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
        sectionDao.deleteByLineId(id);
    }
}
