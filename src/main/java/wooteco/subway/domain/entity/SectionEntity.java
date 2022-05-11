package wooteco.subway.domain.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        return new SectionEntity(null, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
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
        Set<Long> stationIds = new HashSet<>();
        for (SectionEntity sectionEntity : sectionEntities) {
            stationIds.add(sectionEntity.getUpStationId());
            stationIds.add(sectionEntity.getDownStationId());
        }
        return new ArrayList<>(stationIds);
    }
}
