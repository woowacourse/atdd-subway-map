package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.notfoundexception.NotFoundStationException;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());

        if (stationDao.findStationByName(station.getName()).isPresent()) {
            throw new DuplicatedNameException();
        }

        Station newStation = stationDao.save(station);

        return StationResponse.from(newStation);
    }

    public List<Station> findByIds(List<Long> ids) {
        return stationDao.findByIds(ids);
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(NotFoundStationException::new);
    }

    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.findById(id).orElseThrow(NotFoundStationException::new);
        stationDao.delete(id);
    }
}
