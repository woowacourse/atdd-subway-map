package wooteco.subway.station;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.station.DuplicatedStationTitleException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationServiceDto;

@Transactional
@Service
public class StationService {

    private static final int NOT_FOUND = 0;
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station showOne(final long id) {
        return stationDao.showStation(id)
            .orElseThrow(() -> new NotFoundStationException());
    }

    public List<StationServiceDto> showAllDto() {
        List<Station> stations = stationDao.showAll();

        return stations.stream()
            .map(StationServiceDto::from)
            .collect(Collectors.toList());
    }

    public StationServiceDto save(final StationServiceDto stationServiceDto) {
        Station station = stationServiceDto.toEntity();
        String name = stationServiceDto.getName();

        int matchedStationNumber = stationDao.countByName(name);
        if(matchedStationNumber != 0) {
            throw new DuplicatedStationTitleException();
        }

        Station saveStation = stationDao.save(station);
        return StationServiceDto.from(saveStation);
    }

    public void delete(final StationServiceDto stationServiceDto) {
        if (stationDao.delete(stationServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundStationException();
        }
    }
}
