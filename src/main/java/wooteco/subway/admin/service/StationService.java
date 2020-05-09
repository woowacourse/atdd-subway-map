package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public Station findByName(String name) {
		return stationRepository.findByName(name)
				.orElseThrow(() ->
						new IllegalArgumentException("해당 이름의 역을 찾을 수 없습니다."));
	}
}
