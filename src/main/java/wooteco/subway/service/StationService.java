package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.dao.StationJdbcDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ClientException;

@Service
public class StationService {

    private final StationJdbcDao stationDao;

    public StationService(final StationJdbcDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(StationRequest request) {
        if (!stationDao.isExistByName(request.getName())) {
            Station newStation = stationDao.save(new Station(request.getName()));
            return new StationResponse(newStation.getId(), newStation.getName());
        }
        throw new ClientException("이미 등록된 지하철역입니다.");
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public int delete(Long id) {
        if (stationDao.isExistById(id)) {
            return stationDao.delete(id);
        }
        throw new ClientException("존재하지 않는 역입니다.");
    }
}
