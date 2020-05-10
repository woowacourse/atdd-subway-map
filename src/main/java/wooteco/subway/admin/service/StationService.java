package wooteco.subway.admin.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.exception.DuplicatedValueException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public Set<Station> findAllStations() {
		return stationRepository.findAll();
	}

	public Station save(Station station) {
		boolean isDuplicated = findAllStations()
			.stream()
			.anyMatch(existStation -> existStation.isSameName(station));
		if (isDuplicated) {
			throw new DuplicatedValueException(station.getName());
		}

		return stationRepository.save(station);
	}

	public void delete(Long id) {
		stationRepository.deleteById(id);
	}
}
