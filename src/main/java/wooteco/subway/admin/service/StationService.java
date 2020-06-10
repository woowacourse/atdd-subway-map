package wooteco.subway.admin.service;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.error.AlreadyExistException;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(String name) {
        try {
            return stationRepository.save(new Station(name));
        } catch (DbActionExecutionException e) {
            throw new AlreadyExistException(String.format("역 이름 %s이 이미 저장돼있습니다.", name));
        }

    }

    public Iterable<Station> findAllOfStations() {
        return stationRepository.findAll();
    }

    public void deleteStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        stationRepository.delete(station);

    }

    public Set<Station> findAllOf(Line line) {
        List<Long> stationIds = line.getLineStationsId();
        return stationRepository.findAllById(stationIds);
    }

    public Station findByName(String name) {
        return stationRepository.findByName(name);
    }

    public Station findOrRegister(String name) {
        Station station = stationRepository.findByName(name);
        if (Objects.isNull(station)) {
            station = stationRepository.save(new Station(name));
        }
        return station;
    }
}
