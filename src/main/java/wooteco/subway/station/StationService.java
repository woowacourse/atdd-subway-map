package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.NonIdStationDto;
import wooteco.subway.station.dto.StationDto;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationDto> showStations() {
        List<Station> stations = stationDao.showAll();

        return stations.stream()
            .map(station -> new StationDto(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public StationDto save(final NonIdStationDto nonIdStationDto) {
        String targetName = nonIdStationDto.getName();

        int matchedStationNumber = stationDao.countByName(targetName);
        if(matchedStationNumber != 0) {
            throw new NotFoundStationException("[ERROR] 해당하는 역은 이미 존재합니다.");
        }

        Station station = new Station(targetName);
        Station saveStation = stationDao.save(station);
        return new StationDto(saveStation.getId(), saveStation.getName());
    }

    public void delete(final StationDto stationDto) {
        int deletedStationNumber = stationDao.delete(stationDto.getId());

        if (deletedStationNumber == 0) {
            throw new NotFoundStationException("[ERROR] 존재하지 않는 역입니다.");
        }
    }
}
