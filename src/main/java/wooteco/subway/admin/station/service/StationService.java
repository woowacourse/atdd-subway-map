package wooteco.subway.admin.station.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.common.exception.SubwayException;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.repository.StationRepository;
import wooteco.subway.admin.station.service.dto.StationResponse;

@Service
public class StationService {

	private final StationRepository stationRepository;

	public StationService(final StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@Transactional
	public StationResponse save(final Station station) {
		if (stationRepository.findByName(station.getName()).isPresent()) {
			throw new SubwayException("중복된 이름의 역이 존재합니다.");
		}

		return StationResponse.of(stationRepository.save(station));
	}

	@Transactional(readOnly = true)
	public List<StationResponse> findAll() {
		final List<Station> stations = stationRepository.findAll();
		return StationResponse.listOf(stations);
	}

	@Transactional
	public void delete(Long id) {
		stationRepository.deleteById(id);
	}

}
