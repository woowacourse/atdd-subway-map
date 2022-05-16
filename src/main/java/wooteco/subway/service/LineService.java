package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;

@Service
public class LineService {

    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NOT_EXIST_LINE_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";
    private static final String NOT_EXIST_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(Line line) {
        validateNotDuplicatedName(line.getName());
        Line savedLine = lineDao.save(line);
        Section section = new Section(savedLine.getId(), line.getUpStationId(), line.getDownStationId(),
            line.getDistance());
        sectionDao.save(section);
        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(),
            findLineStations(savedLine.getId()));
    }

    private void validateNotDuplicatedName(String name) {
        lineDao.findByName(name)
            .ifPresent(line -> {
                throw new IllegalStateException(ALREADY_IN_LINE_ERROR_MESSAGE);
            });
    }

    private List<Station> findLineStations(Long lineId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        return sections.getAllStationId().stream()
            .map(this::findStationById)
            .collect(Collectors.toList());
    }

    private Station findStationById(Long id) {
        return stationDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_STATION_ID_ERROR_MESSAGE));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Line savedLine = lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(), findLineStations(id));
    }

    public void update(Long id, Line line) {
        validateExistLine(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        validateExistLine(id);
        lineDao.delete(id);
    }

    private void validateExistLine(Long id) {
        lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
    }
}
