package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.service.dto.StationServiceDto;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private static final int NOT_FOUND = 0;
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationServiceDto> showStations() {
        List<Station> stations = stationDao.showAll();

        return stations.stream()
            .map(station -> new StationServiceDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public StationServiceDto save(final StationServiceDto stationServiceDto) {
        Station station = new Station(stationServiceDto.getName());
        Station saveStation = stationDao.save(station);

        return new StationServiceDto(saveStation.getId(), saveStation.getName());
    }

    public void delete(final StationServiceDto stationServiceDto) {
        if (stationDao.delete(stationServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundStationException("[ERROR] 존재하지 않는 역입니다.");
        }
    }
}
