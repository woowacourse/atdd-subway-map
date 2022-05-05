package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import wooteco.subway.domain.Station;

public class MemoryStationDao implements StationDao {

	private Long seq = 0L;
	private final List<Station> stations = new ArrayList<>();

	@Override
	public Long save(Station station) {
		Station newStation = new Station(++seq, station.getName());
		stations.add(newStation);
		return newStation.getId();
	}

	@Override
	public List<Station> findAll() {
		return stations;
	}

	@Override
	public void remove(Long id) {
		stations.remove(findById(id));
	}

	@Override
	public Station findById(Long id) {
		return stations.stream()
			.filter(station -> station.isSameId(id))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당 id에 맞는 지하철 역이 없습니다."));
	}

	@Override
	public Boolean existsByName(String name) {
		return stations.stream()
			.anyMatch(station -> station.isSameName(name));
	}
}
