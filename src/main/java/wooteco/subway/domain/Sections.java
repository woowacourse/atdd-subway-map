package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionEntity;

public class Sections {

    private final SectionMap sectionMap;
    private final SectionDistanceMap sectionDistanceMap;

    private Sections(SectionMap sectionMap,
                     SectionDistanceMap sectionDistanceMap) {
        this.sectionMap = sectionMap;
        this.sectionDistanceMap = sectionDistanceMap;
    }

    public static Sections of(List<SectionEntity> entities) {
        List<Section> sections = entities.stream()
                .map(entity -> new Section(entity.getUpStationId(), entity.getDownStationId()))
                .collect(Collectors.toList());

        return new Sections(SectionMap.of(sections), SectionDistanceMap.of(entities));
    }

    public boolean isExistingStation(Long stationId) {
        boolean isUpStation = sectionMap.hasDownStation(stationId);
        boolean isDownStation = sectionMap.hasUpStation(stationId);
        return !isUpStation && !isDownStation;
    }

    public boolean hasSingleSection() {
        return sectionMap.getSize() == 1;
    }

    public boolean isMiddleStation(Long stationId) {
        boolean isUpStation = sectionMap.hasDownStation(stationId);
        boolean isDownStation = sectionMap.hasUpStation(stationId);
        return isUpStation && isDownStation;
    }

    public SectionEntity toConnectedSectionBetween(Long lineId, Long stationId) {
        return new SectionEntity(lineId,
                sectionMap.getUpStationIdOf(stationId),
                sectionMap.getDownStationIdOf(stationId),
                sectionDistanceMap.getCombinedDistanceOverStationOf(stationId));
    }
}
