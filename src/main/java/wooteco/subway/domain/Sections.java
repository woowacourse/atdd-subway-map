package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionEntity;

public class Sections {

    private static final String STATION_NOT_REGISTERED_EXCEPTION = "구간에 등록되지 않은 지하철역입니다.";
    private static final String ALL_STATIONS_REGISTERED_EXCEPTION = "이미 노선에 등록된 지하철역들입니다.";
    private static final String NO_STATION_REGISTERED_EXCEPTION = "적어도 하나의 지하철역은 이미 노선에 등록되어 있어야 합니다.";

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

    public boolean isEndSection(Long upStationId, Long downStationId) {
        return sectionMap.isFinalUpStation(downStationId) ||
                sectionMap.isFinalDownStation(upStationId);
    }

    public void validateRegisteredStation(Long stationId) {
        if (isRegisteredStation(stationId)) {
            throw new IllegalArgumentException(STATION_NOT_REGISTERED_EXCEPTION);
        }
    }

    public void validateSingleRegisteredStation(Long stationId1, Long stationId2) {
        boolean isRegisteredStation1 = isRegisteredStation(stationId1);
        boolean isRegisteredStation2 = isRegisteredStation(stationId2);
        if (isRegisteredStation1 && isRegisteredStation2) {
            throw new IllegalArgumentException(ALL_STATIONS_REGISTERED_EXCEPTION);
        }
        if (!isRegisteredStation1 && !isRegisteredStation2) {
            throw new IllegalArgumentException(NO_STATION_REGISTERED_EXCEPTION);
        }
    }

    public boolean isRegisteredUpStation(Long stationId) {
        return sectionMap.hasDownStation(stationId);
    }

    public int calculateRemainderDistance(Long upStationId,
                                          Long downStationId,
                                          int distance) {
        validateNewSectionDistance(upStationId, downStationId, distance);
        if (isRegisteredUpStation(upStationId)) {
            return sectionDistanceMap.getRemainderDistanceToDownStation(distance, upStationId);
        }
        return sectionDistanceMap.getRemainderDistanceToUpStation(distance, downStationId);
    }

    public Long getCurrentDownStationOf(Long upStationId) {
        return sectionMap.getDownStationIdOf(upStationId);
    }

    public Long getCurrentUpStationOf(Long downStationId) {
        return sectionMap.getUpStationIdOf(downStationId);
    }

    private void validateNewSectionDistance(Long upStationId,
                                            Long downStationId,
                                            int distance) {
        if (isRegisteredUpStation(upStationId)) {
            sectionDistanceMap.validateCloserThanPreviousSectionFromUpStation(distance, upStationId);
            return;
        }
        sectionDistanceMap.validateCloserThanPreviousSectionFromDownStation(distance, downStationId);
    }

    private boolean isRegisteredStation(Long stationId) {
        boolean isUpStation = sectionMap.hasDownStation(stationId);
        boolean isDownStation = sectionMap.hasUpStation(stationId);
        return !isUpStation && !isDownStation;
    }
}
