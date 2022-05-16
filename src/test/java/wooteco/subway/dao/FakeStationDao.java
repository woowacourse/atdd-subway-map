package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.datanotfound.DataNotFoundException;
import wooteco.subway.exception.datanotfound.StationNotFoundException;

public class FakeStationDao implements StationDao {

    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static void init() {
        seq = 0L;
        stations = new ArrayList<>();
        stations.add(new Station(++seq, "선릉역"));
        stations.add(new Station(++seq, "잠실역"));
        stations.add(new Station(++seq, "강남역"));
    }

    @Override
    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Station findById(long id) {
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findAny()
                .orElseThrow(() -> new StationNotFoundException("존재하지 않는 역입니다."));
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public int deleteStation(long id) {
        int beforeSize = stations.size();
        stations = stations.stream()
                .filter(station -> station.getId() != id)
                .collect(Collectors.toList());

        if (stations.size() < beforeSize) {
            return 1;
        }
        return 0;
    }
}
