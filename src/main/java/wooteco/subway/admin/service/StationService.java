package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.controller.request.StationCreateControllerRequest;
import wooteco.subway.admin.dto.service.response.StationCreateServiceResponse;
import wooteco.subway.admin.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationCreateServiceResponse findBy(Long id) {
		Station persistStation = stationRepository.findById(id)
				.orElseThrow(() ->
						new IllegalArgumentException("해당 id의 역을 찾을 수 없습니다."));
		return StationCreateServiceResponse.of(persistStation);
	}

	public List<StationCreateServiceResponse> findAll() {
		List<StationCreateServiceResponse> stationResponses = new ArrayList<>();
		stationRepository.findAll()
				.forEach(station -> stationResponses.add(StationCreateServiceResponse.of(station)));

		return stationResponses;
	}

	public StationCreateServiceResponse save(StationCreateControllerRequest request) {
		Station persistStation = stationRepository.save(request.toStation());
		return StationCreateServiceResponse.of(persistStation);
	}

	public void deleteBy(Long id) {
		stationRepository.deleteById(id);
	}
}
