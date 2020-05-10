package wooteco.subway.admin.service;

import static java.util.stream.Collectors.*;

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

	public List<StationResponse> findAll() {
		return stationRepository.findAll()
		                        .stream()
		                        .map(StationResponse::of)
		                        .collect(toList());
	}

	public StationResponse save(Station station) {
		return StationResponse.of(stationRepository.save(station));
	}

	public void delete(Long id) {
		stationRepository.deleteById(id);
	}

}
