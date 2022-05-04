package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Repository;

import wooteco.subway.domain.Station;

@Repository
public class StationDao {

	private final List<Station> stations = new ArrayList<>();
	private Long seq = 0L;

	public Long save(Station station) {
		Station newStation = new Station(++seq, station.getName());
		stations.add(newStation);
		return newStation.getId();
	}

	public List<Station> findAll() {
		return stations;
	}

	public void remove(Long id) {
		stations.remove(findById(id));
	}

	public Station findById(Long id) {
		return stations.stream()
			.filter(station -> station.getId().equals(id))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당 id에 맞는 지하철 역이 없습니다."));
	}

	public Boolean existsByName(String name) {
		return stations.stream()
			.anyMatch(station -> name.equals(station.getName()));
	}
}
