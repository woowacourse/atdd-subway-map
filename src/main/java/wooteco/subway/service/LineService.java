package wooteco.subway.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.LineJdbcDao;
import wooteco.subway.dao.SectionJdbcDao;
import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.ClientException;

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
        try {
            Line line = lineDao.save(request);
            sectionJdbcDao.save(line.getId(), new Section(line.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));

            Station upsStation = stationDao.findById(request.getUpStationId());
            Station downStation = stationDao.findById(request.getDownStationId());
            return new LineResponse(line.getId(), line.getName(), line.getColor(), Set.of(upsStation, downStation));
        } catch (DataAccessException exception) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lineDao.findAll()) {
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

    public Sections findSections(Long id) {
        Sections sections = sectionJdbcDao.findById(id);
        return new Sections(sections.linkSections());
    }

    private Map<Long, Station> toMapStations() {
        return stationDao.findAll()
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
    }

    public int update(Long id, LineRequest lineRequest) {
        try {
            return lineDao.update(id, lineRequest);
        } catch (DataAccessException exception) {
            throw new ClientException("등록된 지하철노선으로 변경할 수 없습니다.");
        }
    }

    public int delete(Long id) {
        return lineDao.delete(id);
    }
}

