package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(final StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@Transactional
	public Station save(final Station station) {
		return stationRepository.save(station);
	}

	@Transactional(readOnly = true)
	public List<Station> findAll() {
		return stationRepository.findAll();
	}

	@Transactional
	public void deleteById(final Long id) {
		stationRepository.deleteById(id);
	}
}
