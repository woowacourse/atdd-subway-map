package wooteco.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

@Service
public class StationService {

    private static final String ALREADY_IN_STATION_ERROR_MESSAGE = "이미 해당 이름의 역이 있습니다.";
    private static final String NOT_EXIST_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역이 없습니다.";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        validateDuplicatedName(station.getName());
        return stationDao.save(station);
    }

    private void validateDuplicatedName(String name) {
        stationDao.findByName(name)
            .ifPresent(station -> {
                throw new IllegalStateException(ALREADY_IN_STATION_ERROR_MESSAGE);
            });
    }

    public Station findById(Long id) {
        Optional<Station> station = stationDao.findById(id);
        return station.orElseThrow(() -> new NoSuchElementException(NOT_EXIST_STATION_ID_ERROR_MESSAGE));
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public List<Station> findByIds(List<Long> ids) {
        return ids.stream()
            .map(this::findById)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        validateExistId(id);
        stationDao.delete(id);
    }

    private void validateExistId(Long id) {
        stationDao.findById(id)
            .orElseThrow(() -> new NoSuchElementException(NOT_EXIST_STATION_ID_ERROR_MESSAGE));
    }
}
