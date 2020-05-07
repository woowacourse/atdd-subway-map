package wooteco.subway.admin.service;

import org.springframework.stereotype.Service;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(Station station){
        return stationRepository.save(station);
    }

    public List<Station> findAll(){
        return stationRepository.findAll();
    }

    public void delete(Long id){
        stationRepository.deleteById(id);
    }
}
