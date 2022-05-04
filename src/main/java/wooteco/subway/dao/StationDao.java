package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StationDao {
    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    public Station save(Station station) {
        validateDistinct(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private void validateDistinct(Station otherStation) {
        boolean isDuplicated = stations.stream()
            .anyMatch(station -> station.hasSameNameWith(otherStation));
        if (isDuplicated) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<Station> findAll() {
        return stations;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public void deleteById(Long id) {
        final boolean isRemoved = stations.removeIf(station -> station.getId().equals(id));
        validateRemoved(isRemoved);
    }

    private void validateRemoved(boolean isRemoved) {
        if (!isRemoved) {
            throw new IllegalStateException("삭제하고자 하는 역이 존재하지 않습니다.");
        }
    }
}
