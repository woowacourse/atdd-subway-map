package wooteco.subway.station.dao;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.DuplicatedStationNameException;
import wooteco.subway.station.Station;

public class StationDaoCache implements StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        validateDuplicate(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private void validateDuplicate(Station station) {
        if(stations.stream()
            .map(Station::getName)
            .anyMatch(name -> name.equals(station.getName()))) {
            throw new DuplicatedStationNameException("[ERROR] 역의 이름이 중복됩니다.");
        }
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void clean() {
        stations = new ArrayList<>();
    }
}
