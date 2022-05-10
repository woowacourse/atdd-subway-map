package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.exception.StationDuplicateException;
import wooteco.subway.exception.StationNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station create(final StationRequest stationRequest) {
        final Station station = stationRequest.toEntity();
        checkDuplicateName(station);
        return stationDao.save(station);
    }

    private void checkDuplicateName(final Station station) {
        if (stationDao.existsName(station)) {
            throw new StationDuplicateException("[ERROR] 이미 존재하는 지하철역 이름입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Station> show() {
        return stationDao.findAll();
    }

    @Transactional
    public void delete(final Long id) {
        stationDao.findById(id)
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));
        stationDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Station> findUpAndDownStations(final LineRequest lineRequest) {
        final Station upStation = stationDao.findById(lineRequest.getUpStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));
        final Station downStation = stationDao.findById(lineRequest.getDownStationId())
            .orElseThrow(() -> new StationNotFoundException("[ERROR] 해당 이름의 지하철역이 존재하지 않습니다."));
        return List.of(upStation, downStation);
    }
}
