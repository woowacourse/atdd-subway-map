package wooteco.subway.station.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.common.exception.AlreadyExistsException;
import wooteco.subway.common.exception.NotFoundException;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse save(final StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        validateDuplicate(station);
        Station savedStation = stationDao.save(station);
        return new StationResponse(savedStation.id(), savedStation.name());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.id(), station.name()))
                .collect(Collectors.toList());
    }

    private void validateDuplicate(final Station station) {
        if (stationDao.findByName(station.name()).isPresent()) {
            throw new AlreadyExistsException("이미 있는 역임!");
        }
    }

    public void delete(final Long id) {
        Station station = stationDao.findById(id).orElseThrow(() -> new NotFoundException("없는 역임!"));
        stationDao.delete(station.id());
    }
}
