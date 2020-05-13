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
		validateName(request.getName());
		Station station = request.toStation();
		Station created = stationRepository.save(station);
		return StationResponse.of(created);
	}

	private void validateName(String name) {
		if (name.isEmpty()) {
			throw new IllegalArgumentException("값을 입력해주세요.");
		}
		if (name.contains(" ")) {
			throw new IllegalArgumentException("공백 없이 입력해주세요.");
		}
		for (char c : name.toCharArray()) {
			if (Character.isDigit(c)) {
				throw new IllegalArgumentException("숫자 없이 입력해주세요.");
			}
		}
		if (stationRepository.findByName(name).isPresent()) {
			throw new IllegalArgumentException("역 이름은 중복될수 없습니다.");
		}
	}

	@Transactional
	public void delete(Long id) {
		stationRepository.deleteById(id);
	}
}
