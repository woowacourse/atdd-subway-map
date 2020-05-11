package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse save(Station station) {
		Station persistStation = stationRepository.save(station);
		return StationResponse.of(persistStation);
	}

	public List<StationResponse> findAll() {
		List<Station> stations = stationRepository.findAll();
		return StationResponse.listOf(stations);
	}

	public void deleteById(Long id) {
		stationRepository.deleteById(id);
	}
}
