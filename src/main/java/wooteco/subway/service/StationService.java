package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.repository.StationRepository;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station createStation(String name) {
        Station station = new Station(name);
        long id = stationRepository.save(station);
        return findById(id);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station findById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new SubwayException(ExceptionStatus.ID_NOT_FOUND));
    }

    public void deleteById(long id) {
        stationRepository.deleteById(id);
    }
}
