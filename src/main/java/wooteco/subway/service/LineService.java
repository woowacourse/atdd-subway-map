package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

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
        validateDuplicatedName(line.getName());
        Line savedLine = lineDao.save(line);
        Section section = new Section(savedLine.getId(), line.getUpStationId(), line.getDownStationId(),
            line.getDistance());
        sectionDao.save(section);
        List<Station> stations = new ArrayList<>();
        stations.add(findStationById(line.getUpStationId()));
        stations.add(findStationById(line.getDownStationId()));
        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(), stations);
    }

    private void validateDuplicatedName(String name) {
        lineDao.findByName(name)
            .ifPresent(line -> {
                throw new IllegalStateException(ALREADY_IN_LINE_ERROR_MESSAGE);
            });
    }

    private Station findStationById(Long id) {
        Optional<Station> station = stationDao.findById(id);
        return station.orElseThrow(() -> new NoSuchElementException(NOT_EXIST_STATION_ID_ERROR_MESSAGE));
    }

    public List<Line> findAll() {
        return lineDao.findAll();
    }

    public Line findById(Long id) {
        Optional<Line> line = lineDao.findById(id);
        Line savedLine = line.orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
        Sections sections = new Sections(sectionDao.findAllByLineId(id));
        List<Long> ids = sections.getAllStationId();
        return new Line(savedLine.getId(), savedLine.getName(), savedLine.getColor(), findStationsByIds(ids));
    }

    private List<Station> findStationsByIds(List<Long> ids) {
        return ids.stream()
            .map(this::findStationById)
            .collect(Collectors.toList());
    }

    public void update(Long id, Line line) {
        validateExistId(id);
        lineDao.update(id, line);
    }

    public void delete(Long id) {
        validateExistId(id);
        lineDao.delete(id);
    }

    private void validateExistId(Long id) {
        lineDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_LINE_ID_ERROR_MESSAGE));
    }
}
