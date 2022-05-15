package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.Stations;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ExceptionMessage;
import wooteco.subway.exception.domain.StationException;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest request) {
        Station station = new Station(request.getName());

        try {
            Station savedStation = stationDao.save(station);
            return StationResponse.of(savedStation);
        } catch (DuplicateKeyException e) {
            throw new StationException(ExceptionMessage.DUPLICATED_STATION_NAME.getContent());
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Station> getSortedStations(Sections sections) {
        List<Long> sortedStationId = sections.getSortedStationId();
        List<Station> stations = stationDao.findByIds(sortedStationId);
        return new Stations(stations).sortBy(sortedStationId);
    }
}
