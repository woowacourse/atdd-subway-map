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

    public static class Builder {

        private final Long lineId;
        private final Long upStationId;
        private final Long downStationId;
        private final Integer distance;

        private Long id;

        public Builder(Long lineId, Long upStationId, Long downStationId, Integer distance) {
            this.lineId = lineId;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
            this.distance = distance;
        }

        public Builder(Section section) {
            lineId = section.getLineId();
            upStationId = section.getUpStationId();
            downStationId = section.getDownStationId();
            distance = section.getDistance();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public SectionEntity build() {
            return new SectionEntity(this);
        }

        public static List<SectionEntity> buildMany(List<Section> sections) {
            return sections.stream()
                    .map(it -> new Builder(it).id(it.getId()).build())
                    .collect(Collectors.toList());
        }
    }

    private SectionEntity(Builder builder) {
        id = builder.id;
        lineId = builder.lineId;
        upStationId = builder.upStationId;
        downStationId = builder.downStationId;
        distance = builder.distance;
    }

    public SectionEntity addId(Long id) {
        this.id = id;
        return this;
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
