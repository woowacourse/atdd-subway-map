package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.VoidStationException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationDto;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationDto> showStations() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
            .map(station -> new StationDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public StationDto save(StationDto stationDto) {
        Station station = new Station(stationDto.getName());
        Station saveStation = stationDao.save(station);
        return new StationDto(saveStation.getId(), saveStation.getName());
    }

    public void delete(StationDto stationDto) {
        int deletedStationNumber = stationDao.delete(stationDto.getId());
        if (deletedStationNumber == 0) {
            throw new VoidStationException("[ERROR] 존재하지 않는 역입니다.");
        }
    }
}
