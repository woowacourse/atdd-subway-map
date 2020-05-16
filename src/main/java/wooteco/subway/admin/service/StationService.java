package wooteco.subway.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	public StationResponse save(StationCreateRequest stationCreateRequest) {
		Station station = stationCreateRequest.toStation();
		return StationResponse.of(stationRepository.save(station));
	}

	public List<Station> findAll() {
		return stationRepository.findAll();
	}

	public void delete(Long id) {
		stationRepository.deleteById(id);
	}

	public StationResponse findBy(Long id) {
		return StationResponse.of(stationRepository.findById(id)
		.orElseThrow(() -> new NoSuchElementException("역을 찾을 수 없습니다.")));
	}
}
