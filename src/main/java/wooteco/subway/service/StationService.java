package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public long save(final StationRequest stationRequest) {
        Station station = convertStation(stationRequest);
        validateName(station);
        return stationDao.save(station);
    }

    public List<StationResponse> findAll() {
        return convertStationResponses(stationDao.findAll());
    }

    @Transactional
    public void delete(final Long id) {
        validateExistedLine(id);
        stationDao.delete(id);
    }

    private void validateName(final Station station) {
        if (stationDao.existStationByName(station.getName())) {
            throw new IllegalArgumentException("지하철역 이름이 중복됩니다.");
        }
    }

    private void validateExistedLine(final Long id) {
        if (!stationDao.existStationById(id)) {
            throw new IllegalArgumentException("존재하지 않는 지하철역입니다.");
        }
    }

    private Station convertStation(final StationRequest stationRequest) {
        return new Station(stationRequest.getName());
    }

    private List<StationResponse> convertStationResponses(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }
}
