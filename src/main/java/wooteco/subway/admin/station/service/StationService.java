package wooteco.subway.admin.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.admin.common.exception.AlreadySavedException;
import wooteco.subway.admin.station.domain.Station;
import wooteco.subway.admin.station.domain.repository.StationRepository;
import wooteco.subway.admin.station.service.dto.StationCreateRequest;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Long save(final StationCreateRequest stationCreateRequest) {
        Station station = new Station(stationCreateRequest.getName());
        stationRepository.findByName(station.getName())
                .ifPresent(this::alreadySavedException);
        return stationRepository.save(station).getId();
    }

    private void alreadySavedException(Station station) {
        throw new AlreadySavedException(String.format("역(%s)", station.getName()));
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
