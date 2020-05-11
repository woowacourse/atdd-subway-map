package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.StationCreateRequest;
import wooteco.subway.admin.dto.response.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse findBy(Long id) {
		Station persistStation = stationRepository.findById(id)
				.orElseThrow(() ->
						new IllegalArgumentException("해당 이름의 역을 찾을 수 없습니다."));
		return StationResponse.of(persistStation);
	}

	public List<StationResponse> findAll() {
		List<StationResponse> stationResponses = new ArrayList<>();
		stationRepository.findAll()
				.forEach(station -> stationResponses.add(StationResponse.of(station)));

		return stationResponses;
	}

	public StationResponse save(StationCreateRequest request) {
		Station persistStation = stationRepository.save(request.toStation());
		return StationResponse.of(persistStation);
	}

	public void deleteBy(Long id) {
		stationRepository.deleteById(id);
	}
}
