package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationDao {
	private Long seq = 0L;
	private final List<Station> stations = new ArrayList<>();

	public Station save(Station station) {
		Station persistStation = createNewObject(station);
		stations.add(persistStation);
		return persistStation;
	}

	private Station createNewObject(Station station) {
		Field field = ReflectionUtils.findField(Station.class, "id");
		Objects.requireNonNull(field).setAccessible(true);
		ReflectionUtils.setField(field, station, ++seq);
		return station;
	}

	public List<Station> findAll() {
		return stations;
	}
}
