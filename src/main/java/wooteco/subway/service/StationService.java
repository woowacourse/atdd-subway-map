package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DataNotFoundException;
import wooteco.subway.exception.DuplicateStationException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse create(StationRequest stationRequest) {
        Station station = stationRequest.toEntity();
        try {
            Station newStation = stationDao.save(station);
            return new StationResponse(newStation);
        } catch (DuplicateKeyException e) {
            throw new DuplicateStationException("이미 존재하는 역입니다.");
        }
    }

    public List<StationResponse> showAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    public void delete(Long id) {
        validateExist(id);
        stationDao.deleteById(id);
    }

    private void validateExist(Long id) {
        try {
            stationDao.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("존재하지 않는 역입니다.");
        }
    }

}
