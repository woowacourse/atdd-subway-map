package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.application.exception.DuplicateStationNameException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;
    private final StationDao stationDao;

    public StationService(StationRepository stationRepository,
                          StationDao stationDao) {
        this.stationRepository = stationRepository;
        this.stationDao = stationDao;
    }

    public Station save(StationRequest request) {
        if (stationRepository.existByName(request.getName())) {
            throw new DuplicateStationNameException(request.getName());
        }
        return stationRepository.save(new Station(request.getName()));
    }

    @Transactional(readOnly = true)
    public Station findById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NotFoundStationException(id));
    }

    public void deleteById(Long id) {
        if (!stationRepository.existById(id)) {
            throw new NotFoundStationException(id);
        }
        stationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public StationResponse queryById(Long id) {
        return stationDao.queryById(id)
            .orElseThrow(() -> new NotFoundStationException(id));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> queryAll() {
        return stationDao.queryAll();
    }
}
