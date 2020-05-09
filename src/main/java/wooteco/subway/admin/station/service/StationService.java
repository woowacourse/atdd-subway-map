package wooteco.subway.admin.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.domain.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Long save(final Station station) {
        stationRepository.findByName(station.getName())
                .ifPresent(this::throwAlreadyExistName);
        return stationRepository.save(station).getId();
    }

    private void throwAlreadyExistName(Station station) {
        throw new IllegalArgumentException(station.getName() + " : 이미 존재하는 역 이름입니다.");
    }

    @Transactional(readOnly = true)
    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    @Transactional
    public void deleteById(final Long id) {
        stationRepository.deleteById(id);
    }
}
