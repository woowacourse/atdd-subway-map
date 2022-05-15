package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateStationNameException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional(readOnly = true)
public class StationService {

    private final StationRepository stationRepository;
    private final StationDao stationDao;

    public StationService(StationRepository stationRepository,
                          StationDao stationDao) {
        this.stationRepository = stationRepository;
        this.stationDao = stationDao;
    }

    @Transactional
    public Station save(StationRequest request) {
        if (stationRepository.existByName(request.getName())) {
            throw new DuplicateStationNameException(request.getName());
        }
        return stationRepository.save(new Station(request.getName()));
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NotFoundStationException(id));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!stationRepository.existById(id)) {
            throw new NotFoundStationException(id);
        }
        stationRepository.deleteById(id);
    }

    public StationResponse getById(Long id) {
        return stationDao.queryById(id)
            .orElseThrow(() -> new NotFoundStationException(id));
    }

    public List<StationResponse> getAll() {
        return stationDao.queryAll();
    }
}
