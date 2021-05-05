package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationDto> showStations() {
        List<Station> stations =  stationDao.findAll();
        return stations.stream()
            .map(station -> new StationDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public StationDto save(StationDto stationDto) {
        Station station = new Station(stationDto.getName());
        Station saveStation = stationDao.save(station);
        return new StationDto(saveStation.getId(), saveStation.getName());
    }
}
