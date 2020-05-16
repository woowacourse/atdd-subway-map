package wooteco.subway.admin.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(Station station) {
        if (existsByName(station.getName())) {
            throw new DuplicateKeyException("중복되는 값을 생성하실 수 없습니다.");
        }
        return stationRepository.save(station);
    }

    public boolean existsByName(String name) {
        return stationRepository.existsByName(name);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
