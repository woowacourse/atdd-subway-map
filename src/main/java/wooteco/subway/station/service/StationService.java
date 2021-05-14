package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public StationResponse save(final String name) {
        Station station = new Station(name);
        if (stationRepository.doesNameExist(station)) {
            throw new DuplicateStationNameException();
        }
        long lineId = stationRepository.save(station);
        station.setId(lineId);
        return StationResponse.toDto(station);
    }

    public List<StationResponse> findAll() {
        Stations stations = stationRepository.findAll();
        return StationResponse.toDtos(stations);
    }

    @Transactional
    public void delete(final Long id) {
        if (stationRepository.doesIdNotExist(id)) {
            throw new NoSuchStationException();
        }
        stationRepository.deleteById(id);
    }

    public Station findById(final Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(NoSuchStationException::new);
    }
}
