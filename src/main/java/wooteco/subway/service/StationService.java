package wooteco.subway.service; import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station saveStation(final StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void deleteById(final Long id) {
        stationRepository.deleteById(id);
    }
}
