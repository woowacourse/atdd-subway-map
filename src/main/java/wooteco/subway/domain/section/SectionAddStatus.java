package wooteco.subway.domain.section;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public enum SectionAddStatus {

    ADD_MIDDLE_FROM_UP_STATION(SectionAddStatus::existAnyUpStationSame),
    ADD_MIDDLE_FROM_DOWN_STATION(SectionAddStatus::existAnyDownStationSame),
    ADD_NEW_UP_STATION(SectionAddStatus::existAnyNewUpStation),
    ADD_NEW_DOWN_STATION(SectionAddStatus::existAnyNewDownStation),
    ;

    BiPredicate<List<Section>, Section> condition;

    SectionAddStatus(final BiPredicate<List<Section>, Section> condition) {
        this.condition = condition;
    }

    public static SectionAddStatus from(final List<Section> sections, final Section section) {
        return Arrays.stream(values())
            .filter(it -> it.condition.test(sections, section))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("[ERROR] 새로운 구간을 추가할 수 없습니다."));
    }

    private static boolean existAnyUpStationSame(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> isOnlyUpStationSame(section, it.getUpStationId(), it.getDownStationId()));
    }

    private static boolean isOnlyUpStationSame(final Section section, final Long upStationId,
                                               final Long downStationId) {
        return Objects.equals(section.getUpStationId(), upStationId) &&
            !Objects.equals(section.getDownStationId(), downStationId);
    }

    private static boolean existAnyDownStationSame(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> isOnlyDownStationSame(section, it.getUpStationId(), it.getDownStationId()));
    }

    private static boolean isOnlyDownStationSame(final Section section, final Long upStationId,
                                                 final Long downStationId) {
        return !Objects.equals(section.getUpStationId(), upStationId) &&
            Objects.equals(section.getDownStationId(), downStationId);
    }

    private static boolean existAnyNewUpStation(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> addNewUpStationCase(section, it.getUpStationId()));
    }

    private static boolean addNewUpStationCase(final Section section, final Long upStationId) {
        return Objects.equals(section.getDownStationId(), upStationId);
    }

    private static boolean existAnyNewDownStation(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> addNewDownStationCase(section, it.getDownStationId()));
    }

    private static boolean addNewDownStationCase(final Section section, final Long downStationId) {
        return Objects.equals(section.getUpStationId(), downStationId);
    }
}
