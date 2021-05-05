package wooteco.subway.station.service;

import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dao.dto.StationDto;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationService {

    private StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationDto save(StationDto stationDto) {
        Station station = stationDao.save(new Station(stationDto.getName()));
        return StationDto.from(station);
    }

    public List<StationDto> findAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(station -> StationDto.from(station))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
