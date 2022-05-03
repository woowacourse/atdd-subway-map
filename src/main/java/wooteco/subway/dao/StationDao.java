package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import wooteco.subway.domain.Station;

public class StationDao {
	private Long seq = 0L;
	private final List<Station> stations = new ArrayList<>();

	public Long save(Station station) {
		Station newStation = new Station(++seq, station.getName());
		stations.add(newStation);
		return newStation.getId();
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
