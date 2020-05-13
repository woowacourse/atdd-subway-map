package wooteco.subway.admin.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.exception.AlreadyExistNameException;
import wooteco.subway.admin.repository.StationRepository;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station addStation(final Station station) {
        try {
            return stationRepository.save(station);
        } catch (DbActionExecutionException e) {
            if (e.getCause() instanceof DuplicateKeyException) {
                throw new AlreadyExistNameException(station.getName());
            }
            throw e;
        }
    }

    public List<Station> showStations() {
        return stationRepository.findAll();
    }

    public void removeStation(final Long id) {
        stationRepository.deleteById(id);
    }

    public Station findStationById(final Long id) {
        return stationRepository.findById(id)
            .orElseThrow(RuntimeException::new);
    }
}
