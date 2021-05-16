package wooteco.subway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.web.dto.StationResponse;
import wooteco.subway.web.exception.NotFoundException;

@Service
public class StationService {

    private static final String LINE_RESOURCE_NAME = "노선";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public List<StationResponse> findStationsBySections(List<Section> sections) {
        Set<Long> stationIds = stationIds(sections);
        return stationDao.findStationsByIds(stationIds)
                .stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    private Set<Long> stationIds(List<Section> sections) {
        Set<Long> stationIdSet = new HashSet<>();
        for (Section section : sections) {
            stationIdSet.add(section.getUpStationId());
            stationIdSet.add(section.getDownStationId());
        }

        return stationIdSet;
    }

    public List<Station> findAll() {
        return stationDao.findAll();
    }

    public Station findById(Long id) {
        return stationDao.findById(id)
                .orElseThrow(() -> new NotFoundException(LINE_RESOURCE_NAME));
    }

    public Long save(Station station) {
        return stationDao.save(station);
    }

    public void delete(Long id) {
        stationDao.delete(id);
    }
}
