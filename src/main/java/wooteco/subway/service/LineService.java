package wooteco.subway.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineJdbcDao;
import wooteco.subway.dao.SectionJdbcDao;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionsResponse;

@Service
@Transactional
public class LineService {

    private final LineJdbcDao lineDao;
    private final StationJdbcDao stationDao;
    private final SectionJdbcDao sectionJdbcDao;

    public LineService(LineJdbcDao lineDao, StationJdbcDao stationDao, SectionJdbcDao sectionJdbcDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionJdbcDao = sectionJdbcDao;
    }

    public LineResponse save(LineRequest request) {
        Lines lines = lineDao.findAll();
        lines.add(new Line(request.getName(), request.getColor()));

        Line line = lineDao.save(new Line(request.getName(), request.getColor()));
        sectionJdbcDao.save(line.getId(), new Section(line.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));

        Station upsStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), Set.of(upsStation, downStation));
    }

    public List<LineResponse> findAll() {
        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lineDao.findAll().getLines()) {
            responses.add(makeLineResponseWithLinkedStations(line, sectionJdbcDao.findById(line.getId())));
        }
        return responses;
    }

    public LineResponse findById(Long id) {
        Line line = lineDao.findById(id);
        Sections sections = sectionJdbcDao.findById(line.getId());
        return makeLineResponseWithLinkedStations(line, sections);
    }

    private LineResponse makeLineResponseWithLinkedStations(Line line, Sections sections) {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections.linkSections()) {
            stations.add(toMapStations().get(section.getUpStationId()));
            stations.add(toMapStations().get(section.getDownStationId()));
        }
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public SectionsResponse findSections(Long id) {
        Sections sections = sectionJdbcDao.findById(id);
        return new SectionsResponse(sections.linkSections());
    }

    private Map<Long, Station> toMapStations() {
        return stationDao.findAll()
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
    }

    public int update(Long id, LineRequest request) {
        Lines lines = lineDao.findAll();
        Line line = new Line(request.getName(), request.getColor());
        lines.add(line);
        return lineDao.update(id, new Line(line.getName(), line.getColor()));
    }

    public int delete(Long id) {
        return lineDao.delete(id);
    }
}
