package wooteco.subway.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import wooteco.subway.domain.Section;

public class SectionEntity {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionEntity(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionEntity of(Long id, Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return new SectionEntity(id, lineId, upStationId, downStationId, distance);
    }

    public static SectionEntity of(Long id, SectionEntity other) {
        return of(id, other.lineId, other.upStationId, other.getDownStationId(), other.distance);
    }

    public static SectionEntity of(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return of(null, lineId, upStationId, downStationId, distance);
    }

    public static SectionEntity of(Section section) {
        return new SectionEntity(section.getId(), section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
    }

    public static List<SectionEntity> of(List<Section> sections) {
        return sections.stream()
                .map(SectionEntity::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public static List<Long> extractStationIds(List<SectionEntity> sectionEntities) {
        sectionEntities = sortUpToDown(sectionEntities);
        List<Long> stationIds = new ArrayList<>();
        for (SectionEntity sectionEntity : sectionEntities) {
            stationIds.add(sectionEntity.getUpStationId());
        }
        stationIds.add(sectionEntities.get(sectionEntities.size() - 1).getDownStationId());
        return new ArrayList<>(stationIds);
    }

    private static List<SectionEntity> sortUpToDown(List<SectionEntity> sectionEntities) {
        List<SectionEntity> orderedSectionEntities = new ArrayList<>();
        orderedSectionEntities.add(sectionEntities.get(0));

        extendToUp(orderedSectionEntities, sectionEntities);
        extendToDown(orderedSectionEntities, sectionEntities);

        return orderedSectionEntities;
    }

    private static void extendToUp(List<SectionEntity> orderedSectionEntities, List<SectionEntity> sectionEntities) {
        SectionEntity upTerminalSectionEntity = orderedSectionEntities.get(0);
        System.out.println(upTerminalSectionEntity.getUpStationId());

        Optional<SectionEntity> newUpTerminalSection = sectionEntities.stream()
                .filter(it -> it.isAbleToLinkOnDown(upTerminalSectionEntity))
                .findAny();

        if (newUpTerminalSection.isPresent()) {
            orderedSectionEntities.add(0, newUpTerminalSection.get());
            extendToUp(orderedSectionEntities, sectionEntities);
        }
    }

    private static void extendToDown(List<SectionEntity> orderedSectionEntities, List<SectionEntity> sectionEntities) {
        SectionEntity downTerminalSection = orderedSectionEntities.get(orderedSectionEntities.size() - 1);

        Optional<SectionEntity> newDownTerminalSection = sectionEntities.stream()
                .filter(it -> it.isAbleToLinkOnUp(downTerminalSection))
                .findAny();

        if (newDownTerminalSection.isPresent()) {
            orderedSectionEntities.add(newDownTerminalSection.get());
            extendToDown(orderedSectionEntities, sectionEntities);
        }
    }

    private boolean isAbleToLinkOnDown(SectionEntity upTerminalSectionEntity) {
        return downStationId.equals(upTerminalSectionEntity.upStationId);
    }

    private boolean isAbleToLinkOnUp(SectionEntity downTerminalSectionEntity) {
        return upStationId.equals(downTerminalSectionEntity.downStationId);
    }
}
