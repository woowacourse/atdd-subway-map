package wooteco.subway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class LineService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";
    private static final String NONE_LINE_ERROR_MESSAGE = "해당 ID의 노선은 존재하지 않습니다.";
    private static final String NONE_SECTION_ERROR_MESSAGE = "존재하지 않는 역입니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse save(LineCreateRequest line) {
        validDuplicatedName(line.getName());
        validStations(line.getDownStationId(), line.getUpStationId());

        Long lineId = lineDao.save(line);

        SectionRequest sectionRequest = SectionRequest.from(lineId, line);
        Long sectionId = sectionDao.save(sectionRequest);

        Section section = sectionRequest.toEntity(sectionId);
        section.validSection();

        List<StationResponse> stations = generateStationResponses(line.getDownStationId(), line.getUpStationId());
        return new LineResponse(lineId, line.getName(), line.getColor(), stations);
    }

    private void validStations(Long... ids) {
        for (Long id : ids) {
            validStation(id);
        }
    }

    private void validStation(Long id) {
        if (!stationDao.existsById(id)) {
            throw new IllegalArgumentException(NONE_SECTION_ERROR_MESSAGE);
        }
    }

    private List<StationResponse> generateStationResponses(Long... ids) {
        return Arrays.stream(ids)
                .map(id -> StationResponse.from(stationDao.findById(id)))
                .collect(Collectors.toUnmodifiableList());
    }

    public void update(Long id, LineRequest lineRequest) {
        validDuplicatedName(lineRequest.getName());
        lineDao.update(id, lineRequest);
    }

    private void validDuplicatedName(String name) {
        if (lineDao.existsByName(name)) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    public LineResponse findById(Long id) {
        try {
            Line line = lineDao.findById(id);
            return LineResponse.from(line, findStations(id));
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException(NONE_LINE_ERROR_MESSAGE);
        }
    }

    public List<LineResponse> findAll() {
        return lineDao.findAll()
                .stream()
                .map(line -> LineResponse.from(line, findStations(line.getId())))
                .collect(Collectors.toList());
    }

    private List<StationResponse> findStations(Long id) {
        List<Section> sections = sectionDao.findByLineId(id);

        Set<StationResponse> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            Station downStation = stationDao.findById(section.getDownStationId());
            stations.add(StationResponse.from(downStation));

            Station upStation = stationDao.findById(section.getUpStationId());
            stations.add(StationResponse.from(upStation));
        }
        return new ArrayList<>(stations);
    }

    public void deleteById(Long id) {
        lineDao.deleteById(id);
    }
}
