package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.controller.request.StationControllerRequest;
import wooteco.subway.admin.dto.service.response.StationServiceResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationServiceResponse findBy(Long id) {
		Station persistStation = stationRepository.findById(id)
				.orElseThrow(() ->
						new IllegalArgumentException("해당 id의 역을 찾을 수 없습니다."));
		return StationServiceResponse.of(persistStation);
	}

	public List<StationServiceResponse> findAll() {
		List<StationServiceResponse> stationResponses = new ArrayList<>();
		stationRepository.findAll()
				.forEach(station -> stationResponses.add(StationServiceResponse.of(station)));

		return stationResponses;
	}

	public StationServiceResponse save(StationControllerRequest request) {
		Station persistStation = stationRepository.save(request.toStation());
		return StationServiceResponse.of(persistStation);
	}

	public void deleteBy(Long id) {
		stationRepository.deleteById(id);
	}
}
