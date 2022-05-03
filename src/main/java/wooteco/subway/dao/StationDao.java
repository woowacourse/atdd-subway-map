package wooteco.subway.dao;

import org.springframework.util.ReflectionUtils;

import wooteco.subway.domain.Station;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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

	public void remove(Long id) {
		stations.remove(stations.stream()
			.filter(station -> station.getId().equals(id))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당 id에 맞는 지하철 역이 없습니다."))
		);
	}
}
