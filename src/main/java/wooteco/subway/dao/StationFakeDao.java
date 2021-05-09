package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateNameException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationFakeDao implements StationDao {
    static final List<Station> STATIONS = new ArrayList<>();
    private static Long seq = 0L;

    @Override
    public Station save(Station station) {
        if (isDuplicateStationName(station)) {
            throw new DuplicateNameException("이미 저장된 역 이름입니다.");
        }

        Station persistStation = createNewObject(station);
        STATIONS.add(persistStation);
        return persistStation;
    }

    private static boolean isDuplicateStationName(Station station) {
        final String stationName = station.getName();
        return STATIONS.stream().anyMatch(storedStation -> storedStation.getName().equals(stationName));
    }

    private static Station createNewObject(Station station) {
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
        return STATIONS.stream().filter(line -> line.getName().equals(name)).findAny();
    }

    @Override
    public void delete(Long id) {
        STATIONS.removeIf(station -> station.getId().equals(id));
    }
}
