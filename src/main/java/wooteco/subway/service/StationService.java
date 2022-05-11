package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationResponse;

@Service
@Transactional
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse save(String name) {
        validDuplicatedStation(name);
        Long id = stationDao.save(name);
        return new StationResponse(id, name);
    }

    private void validDuplicatedStation(String name) {
        if (stationDao.existByName(name)) {
            throw new IllegalArgumentException("중복된 Station 이 존재합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        stationDao.deleteById(id);
    }
}
