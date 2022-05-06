package wooteco.subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class StationService {

    private static final String NOT_FOUND_MESSAGE = "%d와 동일한 ID의 지하철이 없습니다.";
    public static final String DUPLICATION_MESSAGE = "%s는 중복된 지하철역 이름입니다.";

    private final StationRepository stationRepository;
    private final StationDao stationDao;

    public StationService(StationRepository stationRepository,
                          StationDao stationDao) {
        this.stationRepository = stationRepository;
        this.stationDao = stationDao;
    }

    public StationResponse save(StationRequest request) {
        if (stationRepository.existByName(request.getName())) {
            throw new DuplicateException(String.format(DUPLICATION_MESSAGE, request.getName()));
        }

        Station station = stationRepository.save(new Station(request.getName()));

        return stationDao.queryById(station.getId())
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, station.getId())));
    }

    public void deleteById(Long id) {
        if (!stationRepository.existById(id)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
        }
        stationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    Station findById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> queryAll() {
        return stationDao.queryAll();
    }
}
