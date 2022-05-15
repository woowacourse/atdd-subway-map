package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse create(final StationRequest stationRequest) {
        if (checkExistByName(stationRequest.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 지하철역이 존재합니다.");
        }
        return StationResponse.from(stationDao.save(stationRequest.toEntity()));
    }

    private boolean checkExistByName(final String name) {
        return stationDao.findByName(name).isPresent();
    }

    @Transactional(readOnly = true)
    public List<StationResponse> getAll() {
        return stationDao.findAll()
                .stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void remove(final Long id) {
        if (!checkExistById(id)) {
            throw new IllegalArgumentException("해당 지하철역이 존재하지 않습니다.");
        }
        stationDao.deleteById(id);
    }

    private boolean checkExistById(final Long id) {
        return stationDao.findById(id).isPresent();
    }
}
