package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionEntity;

public class Sections {

    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

    private final SectionStationMap sectionMap;
    private final SectionDistanceMap sectionDistanceMap;

    private Sections(SectionStationMap sectionMap,
                     SectionDistanceMap sectionDistanceMap) {
        this.sectionMap = sectionMap;
        this.sectionDistanceMap = sectionDistanceMap;
    }

    public static Sections of(List<SectionEntity> entities) {
        List<SectionStationInfo> sectionStationInfos = entities.stream()
                .map(SectionEntity::toSectionStationInfo)
                .collect(Collectors.toList());

        return new Sections(SectionStationMap.of(sectionStationInfos), SectionDistanceMap.of(entities));
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

    public boolean isEndSection(Section section) {
        Long upStationId = section.getUpStationId();
        Long downStationId = section.getDownStationId();
        return sectionMap.isFinalUpStation(downStationId) ||
                sectionMap.isFinalDownStation(upStationId);
    }

    public void validateRegisteredStation(Long stationId) {
        if (isRegisteredStation(stationId)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    public void validateSingleRegisteredStation(Section section) {
        boolean isRegisteredUpStation = isRegisteredStation(section.getUpStationId());
        boolean isRegisteredDownStation = isRegisteredStation(section.getDownStationId());
        if (isRegisteredUpStation && isRegisteredDownStation) {
            throw new IllegalArgumentException(ALL_STATIONS_REGISTERED_EXCEPTION);
        }
        if (!isRegisteredUpStation && !isRegisteredDownStation) {
            throw new IllegalArgumentException(NO_STATION_REGISTERED_EXCEPTION);
        }
    }

    public SectionEntity toUpdatedSection(Long lineId,
                                          Section section) {
        if (hasRegisteredUpStation(section)) {
            return toUpdatedDownSectionEntity(lineId, section);
        }
        return toUpdatedUpperSectionEntity(lineId, section);
    }

    private SectionEntity toUpdatedDownSectionEntity(Long lineId, Section section) {
        Long registeredUpStationId = section.getUpStationId();
        Long newStationId = section.getDownStationId();

        int remainderDistance = sectionDistanceMap.getRemainderDistanceToDownStation(
                section.getDistance(), registeredUpStationId);
        Long registeredDownStationId = sectionMap.getDownStationIdOf(registeredUpStationId);
        return new SectionEntity(lineId, newStationId, registeredDownStationId, remainderDistance);
    }

    private SectionEntity toUpdatedUpperSectionEntity(Long lineId, Section section) {
        Long newStationId = section.getUpStationId();
        Long registeredDownStationId = section.getDownStationId();

        int remainderDistance = sectionDistanceMap.getRemainderDistanceToUpStation(
                section.getDistance(), registeredDownStationId);
        Long registeredUpStation = sectionMap.getUpStationIdOf(registeredDownStationId);
        return new SectionEntity(lineId, registeredUpStation, newStationId, remainderDistance);
    }

    public boolean hasRegisteredUpStation(Section section) {
        return sectionMap.hasDownStation(section.getUpStationId());
    }

    private boolean isRegisteredStation(Long stationId) {
        boolean isUpStation = sectionMap.hasDownStation(stationId);
        boolean isDownStation = sectionMap.hasUpStation(stationId);
        return !isUpStation && !isDownStation;
    }
}
