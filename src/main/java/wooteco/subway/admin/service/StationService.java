package wooteco.subway.admin.service;

import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
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
		try {
			return stationRepository.save(station);
		} catch (DbActionExecutionException e) {
			if (e.getCause() instanceof DuplicateKeyException) {
				throw new DuplicatedValueException(station.getName());
			}
			throw e;
		}
	}

	public void delete(Long id) {
		stationRepository.deleteById(id);
	}
}
