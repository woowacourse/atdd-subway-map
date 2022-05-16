package wooteco.subway.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.*;
import wooteco.subway.domain.*;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionsResponse;
import wooteco.subway.exception.ClientException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionJdbcDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionJdbcDao = sectionDao;
    }

    @Transactional
    public LineResponse save(LineRequest request) {
        if (!lineDao.isExistByName(request.getName())) {
            Line line = lineDao.save(new Line(request.getName(), request.getColor()));
            sectionJdbcDao.save(line.getId(), new Section(line.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));

            Station upsStation = stationDao.findById(request.getUpStationId());
            Station downStation = stationDao.findById(request.getDownStationId());
            return new LineResponse(line.getId(), line.getName(), line.getColor(), Set.of(upsStation, downStation));
        }
        throw new ClientException("이미 등록된 지하철노선입니다.");
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lineDao.findAll()) {
            responses.add(makeLineResponseWithLinkedStations(line, sectionJdbcDao.findById(line.getId())));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }
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

    @Transactional(readOnly = true)
    public SectionsResponse findSections(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }

        Sections sections = sectionJdbcDao.findById(id);
        return new SectionsResponse(sections.linkSections());
    }

    private Map<Long, Station> toMapStations() {
        return stationDao.findAll()
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
    }

    @Transactional
    public int update(Long id, LineRequest request) {
        if (!lineDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }

        if (lineDao.isExistByName(request.getName()) && !lineDao.findById(id).isSameName(request.getName())) {
            throw new ClientException("해당 지하철 노선이 존재하고 있습니다.");
        }
        Line line = new Line(request.getName(), request.getColor());
        return lineDao.update(id, new Line(line.getName(), line.getColor()));
    }

    @Transactional
    public int delete(Long id) {
        if (!lineDao.isExistById(id)) {
            throw new ClientException("존재하지 않는 노선입니다.");
        }
        return lineDao.delete(id);
    }
}
