package wooteco.subway.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

@Repository
public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        Station persistStation = createNewObject(station);
        validateDuplicateName(station.getName());
        stations.add(persistStation);
        return persistStation;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    private void validateDuplicateName(String stationName) {
        boolean isDuplicate = stations.stream()
                .anyMatch(station -> station.isSameName(stationName));
        if (isDuplicate) {
            throw new IllegalArgumentException("이름이 중복된 역은 만들 수 없습니다.");
        }
    }

    public List<Station> findAll() {
        return stations;
    }

    public void delete(Long id) {
        Station foundStation = stations.stream()
                .filter(station -> station.isSameId(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 존재하지 않습니다."));
        stations.remove(foundStation);
    }

    public void clear() {
        stations.clear();
    }
}
