package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class SectionService {
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, LineRequest lineRequest) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();

        duplicateInputStations(upStationId, downStationId);
        Section section = new Section(lineId, upStationId, downStationId, lineRequest.getDistance());
        sectionDao.save(section);
    }

    private void duplicateInputStations(Long upStationId, Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("중복된 지하철 역입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findSectionById(Long id) {
        List<Section> sections = sectionDao.findSectionBylineId(id);
        List<Station> stations = stationsOfLine(sections);

        return stations.stream()
                .map(station ->
                        new StationResponse(
                                station.getId(),
                                station.getName()
                        ))
                .collect(Collectors.toList());
    }

    private List<Station> stationsOfLine(List<Section> sections) {
        Map<Station, Station> stationMap = sections.stream().collect(Collectors.toMap(
                Section::getUpStation,
                Section::getDownStation
        ));
        Station firstUpStation = findFirstUpStation(stationMap);
        return orderStationsOfLine(stationMap, firstUpStation);
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

    public void saveSectionOfExistLine(Long lineId, LineRequest lineRequest) {
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        int distance = lineRequest.getDistance();

        duplicateInputStations(upStationId, downStationId);

        List<Section> sections = sectionDao.findSectionBylineId(lineId);
        Station station = findSameStationInSection(upStationId, downStationId, sections);

        Section section = new Section(lineId, upStationId, downStationId, distance);
        updateNewStation(station, sections, section);
        sectionDao.save(section);
    }

    private Station findSameStationInSection(Long upStationId, Long downStationId, List<Section> sections) {
        List<Station> stations = stationsOfLine(sections);
        List<Station> duplicateStation = findStationsOfSectionByStation(upStationId, downStationId, stations);
        if (duplicateStation.size() != 1) {
            throw new IllegalArgumentException("구간은 하나의 역만 중복될 수 있습니다.");
        }

        return duplicateStation.get(0);
    }

    private List<Station> findStationsOfSectionByStation(Long upStationId, Long downStationId, List<Station> stationsOfLine) {
        return stationsOfLine.stream()
                .filter(station -> station.isSame(upStationId) || station.isSame(downStationId))
                .collect(Collectors.toList());
    }

    private void updateNewStation(Station station, List<Section> sections, Section section) {
        Long lineId = section.getLine().getId();
        Long upStationId = section.getUpStation().getId();
        Long downStationId = section.getDownStation().getId();
        int distance = section.getDistance();

        if (station.isSame(upStationId)) {
            Section selectSection = findSelectedSection(sections, true, upStationId);
            validateDistance(selectSection, distance);
            sectionDao.updateUpStation(lineId, upStationId, downStationId, selectSection.getDistance() - distance);
            return;
        }

        Section selectSection = findSelectedSection(sections, false, downStationId);
        validateDistance(selectSection, distance);
        sectionDao.updateDownStation(lineId, downStationId, upStationId, selectSection.getDistance() - distance);
    }

    private void validateDistance(Section selectSection, int distance) {
        if (selectSection.isLessOrSameDistance(distance)) {
            throw new IllegalArgumentException("거리가 현재 존재하는 구간보다 크거나 같습니다!");
        }
    }

    private Section findSelectedSection(List<Section> sections, boolean isUpStation, Long stationId) {
        Function<Section, Station> sectionFunction = findFunction(isUpStation);
        return sections.stream()
                .filter(section -> sectionFunction.apply(section).isSame(stationId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("중복되는 역이 없습니다!!"));
    }

    private Function<Section, Station> findFunction(boolean isUpStation) {
        if (isUpStation) {
            return Section::getUpStation;
        }
        return Section::getDownStation;
    }

    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findSectionBylineId(lineId);
        validSectionSizeWhenDelete(sections);

        sectionDao.deleteByStationId(lineId, stationId);
        addSectionIfNotEndStation(stationId, lineId, sections);
    }

    private void addSectionIfNotEndStation(Long stationId, Long lineId, List<Section> sections) {
        Optional<Section> sectionBySameUpStation = sections.stream()
                .filter(section -> section.getUpStation().isSame(stationId))
                .findAny();
        Optional<Section> sectionBySameDownStation = sections.stream()
                .filter(section -> section.getDownStation().isSame(stationId))
                .findAny();

        if (sectionBySameUpStation.isPresent() && sectionBySameDownStation.isPresent()) {
            sectionDao.save(lineId,
                    sectionBySameUpStation.get().getUpStation().getId(),
                    stationId,
                    sectionBySameUpStation.get().getDistance() + sectionBySameDownStation.get().getDistance());
        }
    }

    private void validSectionSizeWhenDelete(List<Section> sections) {
        List<Station> stations = stationsOfLine(sections);

        if (stations.size() == 2) {
            throw new IllegalArgumentException("종점역만 남은 경우 삭제를 수행할 수 없습니다!");
        }
    }
}
