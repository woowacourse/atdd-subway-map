package wooteco.subway.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

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
	public List<StationResponse> findAll() {
		return stationRepository.findAll().stream()
			.map(StationResponse::of)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteById(final Long id) {
		stationRepository.deleteById(id);
	}
}
