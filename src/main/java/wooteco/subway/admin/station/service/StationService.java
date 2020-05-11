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
                .ifPresent(Station::throwAlreadyExistNameException);
        return stationRepository.save(station).getId();
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
