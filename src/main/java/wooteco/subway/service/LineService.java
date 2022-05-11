package wooteco.subway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        final Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        final Station upStation = stationDao.findById(lineRequest.getUpStationId());
        final Station downStation = stationDao.findById(lineRequest.getDownStationId());

        checkExistStation(upStation);
        checkExistStation(downStation);
        checkDuplicateLine(lineRequest);

        //노선추가
        final Line savedLine = lineDao.save(line);

        //구간정보 추가
        final Section section = new Section(savedLine, upStation, downStation, lineRequest.getDistance());
        sectionDao.save(section);

        return LineResponse.of(savedLine, upStation, downStation);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        List<Line> lines = lineDao.findAll();

        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lines) {
            Map<Long, Long> sections = getSections(line);
            responses.add(LineResponse.of(line,
                    stationDao.findById(getUpStationId(sections)),
                    stationDao.findById(getDownStationId(sections))));
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        checkExistLine(id);
        final Line line = lineDao.findById(id);
        Map<Long, Long> sections = getSections(line);

        return LineResponse.of(line,
                stationDao.findById(getUpStationId(sections)),
                stationDao.findById(getDownStationId(sections)));
    }

    public void updateLine(Long id, String name, String color) {
        checkExistLine(id);
        lineDao.updateById(id, name, color);
    }

    public void deleteLine(Long id) {
        checkExistLine(id);
        lineDao.deleteById(id);
    }

    public void saveSection(Long id, SectionRequest sectionRequest) {
        final Line line = lineDao.findById(id);
        Map<Long, Long> sections = getSections(line);
        final Long lastUpStationId = getUpStationId(sections);
        final Long lastDownStationId = getDownStationId(sections);

        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();
        final int distance = sectionRequest.getDistance();

        checkNotContainSection(sections, upStationId, downStationId);

        //상행종점등록
        if (downStationId == lastUpStationId) {
            sectionDao.save(new Section(line, stationDao.findById(upStationId), stationDao.findById(downStationId), distance));
            return;
        }

        //하행종점등록
        if (upStationId == lastDownStationId) {
            sectionDao.save(new Section(line, stationDao.findById(upStationId), stationDao.findById(downStationId), distance));
            return;
        }

        //상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        if (!sections.keySet().contains(upStationId) && !sections.values().contains(downStationId)) {
            throw new IllegalArgumentException();
        }

        //갈래길 추가
        //1.상행이 같을 경우
        if (sections.keySet().contains(upStationId)) {
            Section existSection = sectionDao.findByUpStationId(upStationId);
            int existDistance = existSection.getDistance();
            Section updateSection = new Section(existSection.getId(), line, stationDao.findById(downStationId),
                    existSection.getDownStation(), existDistance - distance);
            sectionDao.save(new Section(line, stationDao.findById(upStationId), stationDao.findById(downStationId), distance));
            sectionDao.update(updateSection);
            return;
        }

        //2.하행이 같을 경우
        if (sections.values().contains(downStationId)) {
            Section existSection = sectionDao.findByDownStationId(downStationId);
            int existDistance = existSection.getDistance();
            Section updateSection = new Section(existSection.getId(), line, existSection.getUpStation(),
                    stationDao.findById(upStationId), existDistance - distance);
            sectionDao.save(new Section(line, stationDao.findById(upStationId), stationDao.findById(downStationId), distance));
            sectionDao.update(updateSection);
            return;
        }
    }

    private void checkExistLine(Long id) {
        final Line line = lineDao.findById(id);
        if (line == null) {
            throw new IllegalArgumentException("해당하는 노선이 존재하지 않습니다.");
        }
    }

    private void checkExistStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("해당하는 역이 존재하지 않습니다.");
        }
    }

    private void checkDuplicateLine(LineRequest lineRequest) {
        if (lineDao.hasLine(lineRequest.getName())) {
            throw new IllegalArgumentException("같은 이름의 노선이 존재합니다.");
        }
    }

    private Map<Long, Long> getSections(Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        Map<Long, Long> map = new HashMap<>();
        for (Section section : sections) {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            map.put(upStation.getId(), downStation.getId());
        }
        return map;
    }

    private Long getUpStationId(Map<Long, Long> sections) {
        List<Long> keys = new ArrayList<>(sections.keySet());
        List<Long> values = new ArrayList<>(sections.values());
        return keys.stream()
                .filter(key -> !values.contains(key))
                .findFirst()
                .orElseThrow();
    }

    private Long getDownStationId(Map<Long, Long> sections) {
        List<Long> keys = new ArrayList<>(sections.keySet());
        List<Long> values = new ArrayList<>(sections.values());
        return values.stream()
                .filter(value -> !keys.contains(value))
                .findFirst()
                .orElseThrow();
    }

    private void checkNotContainSection(Map<Long, Long> sections, Long upStationId, Long downStationId) {
        final boolean result = sections.keySet()
                .stream()
                .anyMatch(key -> key == upStationId && sections.get(key) == downStationId);
        if (result) {
            throw new IllegalArgumentException();
        }
    }
}
