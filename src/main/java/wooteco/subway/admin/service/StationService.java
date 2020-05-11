package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public List<StationResponse> findAll() {
		return StationResponse.listOf(stationRepository.findAll());
	}

	@Transactional
	public StationResponse create(StationRequest request) {
		Station station = request.toStation();
		Station created = stationRepository.save(station);
		return StationResponse.of(created);
	}

	@Transactional
	public void delete(Long id) {
		stationRepository.deleteById(id);
	}
}
