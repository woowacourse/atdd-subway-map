package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse createStation(StationCreateRequest stationCreateRequest) {
		Station station = stationCreateRequest.toStation();
		Station persistStation = stationRepository.save(station);
		return StationResponse.of(persistStation);
	}

	public List<StationResponse> showStations() {
		List<Station> persistStations = stationRepository.findAll();
		return StationResponse.listOf(persistStations);
	}

	public void deleteStations(Long id) {
		stationRepository.deleteById(id);
	}
}
