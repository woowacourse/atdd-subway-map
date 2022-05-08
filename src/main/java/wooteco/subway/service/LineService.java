package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@Service
public class LineService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NO_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(Line line) {
        validateUniqueName(line.getName());
        Long id = lineDao.save(line);
        return lineDao.findById(id);
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line update(Long id, Line line) {
        validateID(id);
        lineDao.update(id, line);
        return lineDao.findById(id);
    }

    public void delete(Long id) {
        validateID(id);
        lineDao.delete(id);
    }

    private void validateUniqueName(String name) {
        if (lineDao.hasLine(name)) {
            throw new IllegalArgumentException(ALREADY_IN_LINE_ERROR_MESSAGE);
        }
    }

    private void validateID(Long id) {
        if (!lineDao.hasLine(id)) {
            throw new IllegalArgumentException(NO_ID_ERROR_MESSAGE);
        }
    }

    public void addSection(Section section) {
        sectionDao.save(section);
    }

    public List<Station> findStationsOfLine(Line line) {
        List<Station> result = new ArrayList<>();
        List<Section> sections = sectionDao.findAllByLineId(line.getId());

        Station nowStation = stationDao.findById(line.getUpStationId());
        result.add(nowStation);
        while (isNotDownStation(line, nowStation)) {
            Station nextStation = stationDao.findById(getSection(sections, nowStation.getId()).getDownStationId());
            result.add(nextStation);
            nowStation = nextStation;
        }
        return result;
    }

    private boolean isNotDownStation(Line line, Station nowStation) {
        return !Objects.equals(nowStation.getId(), line.getDownStationId());
    }

    private Section getSection(List<Section> sections, Long upStationId) {
        return sections.stream().filter(section -> Objects.equals(section.getUpStationId(), upStationId))
                .findFirst().orElseThrow(() -> new NullPointerException("해당 아이디의 역이 없습니다."));
    }

    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        boolean isUpStation = sections.stream().anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isDownStation = sections.stream().anyMatch(section -> section.getDownStationId().equals(stationId));
        //

    }
}
