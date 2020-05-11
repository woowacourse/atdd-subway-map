package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.exception.AlreadyExistDataException;
import wooteco.subway.admin.exception.NotExistDataException;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
	private StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public Station save(Station station) {
		int sameStationCount = stationRepository.countSameStationByName(station.getName());
		if (sameStationCount == 0) {
			return stationRepository.save(station);
		}
		throw new AlreadyExistDataException("이미 존재하는 역입니다!");
	}

	public Iterable<Station> findAllStations() {
		return stationRepository.findAll();
	}

	public List<Station> findStationByNames(List<String> names) {
		List<Station> stations = stationRepository.findStationsByNames(names);
		if (stations.size() == names.size()) {
			return stations;
		}
		throw new NotExistDataException("찾는 역 중 존재하지 않는 역이 있습니다!");
	}

	public void deleteStationById(Long id) {
		stationRepository.deleteById(id);
	}
}
