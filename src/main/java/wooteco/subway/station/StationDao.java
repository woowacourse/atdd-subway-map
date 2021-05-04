package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationDao {

    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        validateToSave(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private void validateToSave(Station stationToSave) {
        boolean hasSameName = stations.stream().anyMatch(station -> station.hasSameName(stationToSave));

        if (hasSameName) {
            throw new IllegalArgumentException("중복된 이름을 생성할 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        Objects.requireNonNull(field).setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void delete(Long id) {
        boolean isRemoved = stations.removeIf(station -> station.getId().equals(id));

        if (!isRemoved) {
            throw new IllegalArgumentException("존재하지 않는 ID 입니다.");
        }
    }
}
