package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationResponse;
import wooteco.subway.admin.exception.DuplicateStationNameException;
import wooteco.subway.admin.exception.NotFoundStationException;
import wooteco.subway.admin.repository.StationRepository;

@Transactional
@Service
public class StationService {
	private final StationRepository stationRepository;

	public StationService(StationRepository stationRepository) {
		this.stationRepository = stationRepository;
	}

	@Transactional(readOnly = true)
	public List<StationResponse> findAll() {
		return StationResponse.ofList(stationRepository.findAll());
	}

	public Long create(Station station) {
		if (stationRepository.existsByName(station.getName())) {
			throw new DuplicateStationNameException();
		}
		Station createdStation = stationRepository.save(station);
		return createdStation.getId();
	}

	public void delete(Long id) {
		if (!stationRepository.existsById(id)) {
			throw new NotFoundStationException();
		}
		stationRepository.deleteById(id);
	}
}
