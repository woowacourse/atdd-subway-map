package wooteco.subway.repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import wooteco.subway.dao.SequenceDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.StationSeries;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.RowNotFoundException;

@Repository
public class StationRepository {
    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public void persist(StationSeries stationSeries) {
        final List<Station> stations = stationSeries.getStations();
        List<Long> persistedIds = toIds(findAllStations());

        deleteStations(stations, persistedIds);
        updateStations(stations, persistedIds);
    }

    private void deleteStations(List<Station> stations, List<Long> persistedIds) {
        final List<Long> ids = toIds(stations);
        for (Long persistedId : persistedIds) {
            if (!ids.contains(persistedId)) {
                deleteById(persistedId);
            }
        }
    }

    private void updateStations(List<Station> stations, List<Long> persistedIds) {
        for (Station station : stations) {
            if (!persistedIds.contains(station.getId())) {
                save(station);
            }
        }
    }

    private List<Long> toIds(List<Station> stations) {
        return stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());
    }

    public Station findById(Long id) {
        final StationEntity entity = stationDao.findById(id)
            .orElseThrow(() -> new RowNotFoundException(String.format("%d의 id를 가진 역이 존재하지 않습니다.", id)));
        return new Station(entity.getId(), entity.getName());
    }

    public List<Station> findAllStations() {
        return stationDao.findAll()
            .stream()
            .map(entity -> new Station(entity.getId(), entity.getName()))
            .collect(Collectors.toList());
    }

    private Station save(Station station) {
        final Long id = stationDao.save(StationEntity.from(station));
        return injectID(station, id);
    }

    private Station injectID(Station station, Long id) {
        final Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, id);
        return station;
    }

    private void deleteById(Long id) {
        final boolean isDeleted = stationDao.deleteById(id);
        if (!isDeleted) {
            throw new RowNotFoundException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
