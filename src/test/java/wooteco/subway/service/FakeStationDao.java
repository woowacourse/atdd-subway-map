package wooteco.subway.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.ReflectionUtils;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class FakeStationDao implements StationDao {

    private Long seq = 0L;
    private List<Station> stations = new ArrayList<>();

    @Override
    public Station save(Station station) {
        if (isDuplicateName(station)) {
            throw new IllegalArgumentException("중복된 이름의 역은 저장할 수 없습니다.");
        }

        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private boolean isDuplicateName(final Station station) {
        return stations.stream()
                .anyMatch(it -> it.isSameName(station));
    }

    @Override
    public List<Station> findAll() {
        return stations;
    }

    @Override
    public Integer deleteById(Long id) {
        Station foundStation = stations.stream()
                .filter(station -> station.getId().equals(id))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        stations.remove(foundStation);
        return 1;
    }

    private Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }
}
