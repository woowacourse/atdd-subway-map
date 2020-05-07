package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

/**
 *    class description
 *
 *    @author HyungJu An, YeongHo Park
 */
@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse createStation(Station station) {
		return StationResponse.of(stationRepository.save(station));
	}

	public List<StationResponse> showStations() {
		return StationResponse.listOf(stationRepository.findAll());
	}

	public void deleteStation(Long id) {
		stationRepository.deleteById(id);
	}
}
