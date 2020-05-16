package wooteco.subway.admin.service;

import java.util.Set;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
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
		boolean isDuplicated = stationRepository.findByName(station.getName())
			.isPresent();
		if (isDuplicated) {
			throw new DuplicatedValueException(station.getName());
		}
		try {
			return stationRepository.save(station);
		} catch (DbActionExecutionException e) {
			throw new DuplicatedValueException(station.getName());
		}
	}

	public void delete(Long id) {
		stationRepository.deleteById(id);
	}
}
