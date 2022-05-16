package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.duplicate.DuplicateStationException;
import wooteco.subway.exception.invalidrequest.InvalidStationDeleteRequestException;
import wooteco.subway.exception.notfound.StationNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateUnique(station);
        Station newStation = stationDao.save(station);
        return new StationResponse(newStation);
    }

    private void validateUnique(Station station) {
        if (stationDao.existsName(station)) {
            throw new DuplicateStationException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new StationNotFoundException("존재하지 않는 역입니다."));
    }

    public void delete(Long id) {
        validateExist(id);
        validateDeletable(id);
        stationDao.deleteById(id);
    }

    private void validateExist(Long id) {
        if (!stationDao.existsId(id)) {
            throw new StationNotFoundException("존재하지 않는 역입니다.");
        }
    }

    private void validateDeletable(Long id) {
        if (stationDao.existsContainingSection(id)) {
            throw new InvalidStationDeleteRequestException("해당 역을 포함하고 있는 구간이 있어 삭제할 수 없습니다.");
        }
    }

}
