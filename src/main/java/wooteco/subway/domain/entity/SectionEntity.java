package wooteco.subway.domain.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import wooteco.subway.domain.Section;

public class SectionEntity {

    private static final int RANDOM_INDEX = 0;
    private static final int UP_TERMINAL_STATION_INDEX = 0;

    private final Long lineId;
    private final Long upStationId;
    private final Long downStationId;
    private final Integer distance;

    private Long id;

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
        return sectionEntities.stream()
                .flatMap(it -> Stream.of(it.getUpStationId(), it.getDownStationId()))
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<SectionEntity> sortUpToDown(List<SectionEntity> sectionEntities) {
        List<SectionEntity> orderedSectionEntities = new ArrayList<>();
        orderedSectionEntities.add(sectionEntities.get(RANDOM_INDEX));

        extendToUp(orderedSectionEntities, sectionEntities);
        extendToDown(orderedSectionEntities, sectionEntities);

        return orderedSectionEntities;
    }

    private static void extendToUp(List<SectionEntity> orderedSectionEntities, List<SectionEntity> sectionEntities) {
        SectionEntity upTerminalSectionEntity = orderedSectionEntities.get(UP_TERMINAL_STATION_INDEX);
        System.out.println(upTerminalSectionEntity.getUpStationId());

        Optional<SectionEntity> newUpTerminalSection = sectionEntities.stream()
                .filter(it -> it.isAbleToLinkOnDown(upTerminalSectionEntity))
                .findAny();

        if (newUpTerminalSection.isPresent()) {
            orderedSectionEntities.add(UP_TERMINAL_STATION_INDEX, newUpTerminalSection.get());
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
