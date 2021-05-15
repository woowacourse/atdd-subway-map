package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.service.dto.StationServiceDto;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private static final int NOT_FOUND = 0;
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public List<StationServiceDto> showStations() {
        List<Station> stations = stationDao.showAll();

        return stations.stream()
            .map(StationServiceDto::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public StationServiceDto save(@Valid StationServiceDto stationServiceDto) {
        Station station = stationServiceDto.toEntity();
        Station saveStation = stationDao.save(station);

        return StationServiceDto.from(saveStation);
    }

    @Transactional
    public void delete(@Valid StationServiceDto stationServiceDto) {
        if (stationDao.delete(stationServiceDto.getId()) == NOT_FOUND) {
            throw new NotFoundStationException();
        }
    }
}
