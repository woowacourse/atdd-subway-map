package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, Long upStationId, Long downStationId, int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    public List<StationResponse> findSectionById(Long id) {
        List<Station> stationsOfLine = findStationsOfLine(id);

        return stationsOfLine.stream()
                .map(res ->
                        new StationResponse(
                                res.getId(),
                                res.getName()
                        ))
                .collect(Collectors.toList());
    }

    private List<Station> findStationsOfLine(Long lineId) {
        List<Station> stationsOfLine = new ArrayList<>();
        Map<Station, Station> sectionMap = sectionDao.findSectionById(lineId);

        Station key = findFirstUpStation(sectionMap);
        stationsOfLine.add(key);
        while (sectionMap.get(key) != null) {
            key = sectionMap.get(key);
            stationsOfLine.add(key);
        }
        return stationsOfLine;
    }

    private Station findFirstUpStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = new HashSet<>(sectionMap.keySet());
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        upStations.removeAll(downStations);
        return upStations.iterator().next();
    }
}
