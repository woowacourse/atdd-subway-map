package wooteco.subway.station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.exception.DuplicateNameException;

public class StationFakeDao implements StationDao {
    private static Long seq = 0L;
    static final List<Station> STATIONS = new ArrayList<>();

    @Override
    public Station save(StationRequest stationRequest) {
        if (isDuplicateStationName(stationRequest)) {
            throw new DuplicateNameException("이미 저장된 역 이름입니다.");
        }

        Station persistStation = createNewObject(stationRequest);
        STATIONS.add(persistStation);
        return persistStation;
    }

    private static boolean isDuplicateStationName(StationRequest stationRequest) {
        final String stationName = stationRequest.getName();
        return STATIONS.stream()
                       .anyMatch(storedStation -> storedStation.getName().equals(stationName));
    }

    private static Station createNewObject(StationRequest stationRequest) {
        Station station = new Station(seq, stationRequest.getName());
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return STATIONS;
    }

    @Override
    public Optional<Station> findByName(String name) {
        return STATIONS.stream()
                       .filter(line -> line.getName().equals(name))
                       .findAny();
    }

    @Override
    public void delete(Long id) {
        STATIONS.removeIf(station -> station.getId().equals(id));
    }
}
