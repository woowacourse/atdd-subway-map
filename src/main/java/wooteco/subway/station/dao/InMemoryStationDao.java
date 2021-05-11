package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.common.exception.not_found.NotFoundStationInfoException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class InMemoryStationDao implements StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        Station persistStation = setId(station);
        if (isDuplicatedName(station)) {
            throw new IllegalArgumentException(String.format("역 이름이 중복되었습니다. 중복된 역 이름 : %s", station.getName()));
        }
        stations.add(persistStation);
        return persistStation;
    }

    private boolean isDuplicatedName(Station station) {
        return stations.stream()
                .anyMatch(station1 -> station1.getName().equals(station.getName()));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Station findById(Long id) {
        return stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundStationInfoException(String.format("ID에 해당하는 역이 없습니다. ID : %d", id)));
    }

    @Override
    public boolean checkExistName(StationName name) {
        return stations.stream()
                .anyMatch(station -> station.getName().equals(name));
    }

    @Override
    public boolean checkExistId(Long id) {
        return stations.stream()
                .anyMatch(station -> station.getId().equals(id));
    }

    private Station setId(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public void delete(Station station) {
        stations.remove(station);
    }

    @Override
    public void deleteAll() {
        seq = 0L;
        stations = new ArrayList<>();
    }
}
