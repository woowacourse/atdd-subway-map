package wooteco.subway.repository;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.station.Station;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.exception.DuplicateLineColorException;
import wooteco.subway.repository.exception.DuplicateLineNameException;
import wooteco.subway.repository.exception.DuplicateStationNameException;

@Repository
public class SubwayRepository implements LineRepository, StationRepository {

    private final LineDao lineDao;
    private final StationDao stationDao;

    public SubwayRepository(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Override
    public Long saveLine(Line line) {
        validateLineNotDuplicated(line);
        return lineDao.save(line);
    }

    private void validateLineNotDuplicated(Line line) {
        validateLineNameNotDuplicated(line.getName());
        validateLineColorNotDuplicated(line.getColor());
    }

    private void validateLineNameNotDuplicated(String name) {
        if (lineDao.existsByName(name)) {
            throw new DuplicateLineNameException(name);
        }
    }

    private void validateLineColorNotDuplicated(String color) {
        if (lineDao.existsByColor(color)) {
            throw new DuplicateLineColorException(color);
        }
    }

    @Override
    public Long saveStation(Station station) {
        validateStationNotDuplicated(station);
        return stationDao.save(station);
    }

    private void validateStationNotDuplicated(Station station) {
        validateStationNameNotDuplicated(station.getName());
    }

    private void validateStationNameNotDuplicated(String name) {
        if (stationDao.existsByName(name)) {
            throw new DuplicateStationNameException(name);
        }
    }

    @Override
    public List<Line> findLines() {
        return lineDao.findAll();
    }

    @Override
    public Line findLineById(Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new NoSuchElementException("조회하고자 하는 지하철노선이 존재하지 않습니다."));
    }

    @Override
    public List<Station> findStations() {
        return stationDao.findAll();
    }

    @Override
    public Station findStationById(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new NoSuchElementException("조회하고자 하는 지하철역이 존재하지 않습니다."));
    }

    @Override
    public void updateLine(Line line) {
        if (lineDao.findById(line.getId()).isEmpty()) {
            throw new NoSuchElementException("수정하고자 하는 지하철노선이 존재하지 않습니다.");
        }
        validateLineNotDuplicated(line);
        lineDao.update(line.getId(), line.getName(), line.getColor());
    }

    @Override
    public void removeLine(Long lineId) {
        if (lineDao.findById(lineId).isEmpty()) {
            throw new NoSuchElementException("삭제하고자 하는 지하철노선이 존재하지 않습니다.");
        }
        lineDao.remove(lineId);
    }

    @Override
    public void removeStation(Long stationId) {
        if (stationDao.findById(stationId).isEmpty()) {
            throw new NoSuchElementException("삭제하고자 하는 지하철역이 존재하지 않습니다.");
        }
        stationDao.remove(stationId);
    }
}
