package wooteco.subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistException;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(String name) {
        if (stationRepository.existByName(name)) {
            throw new DuplicateException(String.format("%s는 중복된 지하철역 이름입니다.", name));
        }
        return stationRepository.save(new Station(name));
    }

    public void deleteById(Long id) {
        if (!stationRepository.existById(id)) {
            throw new NotExistException(String.format("%d와 동일한 ID의 지하철이 없습니다.", id));
        }
        stationRepository.deleteById(id);
    }
}
