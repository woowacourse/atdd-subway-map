package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(final StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station create(Station station) {
        checkCreateValidation(station);
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void deleteById(Long id) {
        stationRepository.delete(id);
    }

    private void checkCreateValidation(Station station) {
        boolean duplicated = stationRepository.findAll().contains(station);
        if (duplicated) {
            throw new IllegalArgumentException("역 이름이 중복 되었습니다.");
        }
    }
}
