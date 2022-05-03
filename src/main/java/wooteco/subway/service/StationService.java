package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

public class StationService {

	private final StationDao stationDao;

	public StationService(StationDao stationDao) {
		this.stationDao = stationDao;
	}

	public StationDto create(String name) {
		validateNameNotDuplicated(name);
		Long stationId = stationDao.save(new Station(name));
		Station station = stationDao.findById(stationId);
		return StationDto.from(station);
	}

	private void validateNameNotDuplicated(String name) {
		if (stationDao.existsByName(name)) {
			throw new IllegalArgumentException("해당 이름의 지하철 역이 이미 존재합니다.");
		}
	}

	public List<StationDto> listStations() {
		return stationDao.findAll()
			.stream()
			.map(StationDto::from)
			.collect(Collectors.toUnmodifiableList());
	}

	public void remove(Long id) {
		stationDao.remove(id);
	}
}
