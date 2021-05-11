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
        invalidSection(upStationId, downStationId);
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    private void invalidSection(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
    }

    public List<StationResponse> findSectionById(Long id) {
        List<Station> stations = stationsOfLine(id);

        return stations.stream()
                .map(res ->
                        new StationResponse(
                                res.getId(),
                                res.getName()
                        ))
                .collect(Collectors.toList());
    }

    private List<Station> stationsOfLine(Long lineId) {
        Map<Station, Station> sectionMap = sectionDao.findSectionById(lineId);
        Station firstUpStation = findFirstUpStation(sectionMap);
        return orderStationsOfLine(sectionMap, firstUpStation);
    }

    private List<Station> orderStationsOfLine(Map<Station, Station> sectionMap, Station upStation) {
        List<Station> stationsOfLine = new ArrayList<>();
        stationsOfLine.add(upStation);
        while (sectionMap.get(upStation) != null) {
            upStation = sectionMap.get(upStation);
            stationsOfLine.add(upStation);
        }
        return stationsOfLine;
    }

    private Station findFirstUpStation(Map<Station, Station> sectionMap) {
        Set<Station> upStations = new HashSet<>(sectionMap.keySet());
        Set<Station> downStations = new HashSet<>(sectionMap.values());
        upStations.removeAll(downStations);
        return upStations.iterator().next();
    }

    public void saveSectionOfExistLine(Long lineId, Long upStationId, Long downStationId, int distance) {
        invalidSection(upStationId, downStationId);

        List<Station> stations = stationsOfLine(lineId);
        List<Station> duplicateStation = countDuplicateStation(upStationId, downStationId, stations);
        if (duplicateStation.size() != 1) {
            throw new IllegalArgumentException("구간은 하나의 역만 중복될 수 있습니다.");
        }

        Station station = duplicateStation.get(0);
        updateNewStation(station, lineId, upStationId, downStationId);

        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    private void updateNewStation(Station station, Long lineId, Long upStationId, Long downStationId) {
        if (station.getId().equals(upStationId)) {
            sectionDao.updateUpStation(lineId, upStationId, downStationId);
            return;
        }

        sectionDao.updateDownStation(lineId, downStationId, upStationId);
    }

    private List<Station> countDuplicateStation(Long upStationId, Long downStationId, List<Station> stationsOfLine) {
        return stationsOfLine.stream()
                .filter(station -> station.getId().equals(upStationId) || station.getId().equals(downStationId))
                .collect(Collectors.toList());
    }
}
