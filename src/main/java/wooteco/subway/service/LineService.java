package wooteco.subway.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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
public class LineService {

    private final LineJdbcDao lineDao;
    private final StationJdbcDao stationDao;
    private final SectionJdbcDao sectionJdbcDao;

    public LineService(LineJdbcDao lineDao, StationJdbcDao stationDao, SectionJdbcDao sectionJdbcDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionJdbcDao = sectionJdbcDao;
    }

    public LineResponse createLine(LineRequest request) {
        try {
            Line line = lineDao.save(request);
            sectionJdbcDao.save(line.getId(), new Section(0L, line.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));

            Station upsStation = stationDao.findStation(request.getUpStationId());
            Station downStation = stationDao.findStation(request.getDownStationId());
            return new LineResponse(line.getId(), line.getName(), line.getColor(), Set.of(upsStation, downStation));
        } catch (DataAccessException exception) {
            throw new ClientException("이미 등록된 지하철노선입니다.");
        }
    }

    public List<LineResponse> findAll() {
        List<LineResponse> responses = new ArrayList<>();
        for (Line line : lineDao.findAll()) {
            responses.add(new LineResponse());
        }
        return responses;
    }


    private Map<Long, Station> toMapStations() {
        return stationDao.findAll()
                .stream()
                .collect(Collectors.toMap(Station::getId, station -> station));
    }

    public int updateLine(Long id, LineRequest lineRequest) {
        try {
            return lineDao.update(id, lineRequest);
        } catch (DataAccessException exception) {
            throw new ClientException("등록된 지하철노선으로 변경할 수 없습니다.");
        }
    }

    public int deleteLine(Long id) {
        return lineDao.delete(id);
    }
}
