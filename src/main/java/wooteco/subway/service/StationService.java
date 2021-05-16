package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.station.response.StationResponse;
import wooteco.subway.exception.station.StationDeleteException;
import wooteco.subway.exception.station.StationDuplicateException;
import wooteco.subway.exception.station.StationNotExistException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;
    private final SectionService sectionService;

    public StationService(StationDao stationDao, SectionService sectionService) {
        this.stationDao = stationDao;
        this.sectionService = sectionService;
    }

    public StationResponse create(String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new StationDuplicateException(name);
                });
        return new StationResponse(stationDao.insert(name));
    }

    public List<StationResponse> showAll() {
        List<Station> stations = stationDao.findAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public Station showById(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new StationNotExistException(stationId));
    }

    public void deleteById(Long id) {
        if (sectionService.existStationByStationId(id)) {
            throw new StationDeleteException(id);
        }
        stationDao.deleteById(id);
    }
}
