package wooteco.subway.domain.section;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

public enum SectionAddStatus {

    ADD_MIDDLE_FROM_UP_STATION(SectionAddStatus::existAnyUpStationSame),
    ADD_MIDDLE_FROM_DOWN_STATION(SectionAddStatus::existAnyDownStationSame),
    ADD_NEW_UP_STATION(SectionAddStatus::existAnyNewUpStation),
    ADD_NEW_DOWN_STATION(SectionAddStatus::existAnyNewDownStation),
    ;

    final BiPredicate<List<Section>, Section> condition;

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
            .anyMatch(it -> it.isOnlyUpStationSame(section));
    }

    private static boolean existAnyDownStationSame(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> it.isOnlyDownStationSame(section));
    }

    private static boolean existAnyNewUpStation(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> it.addNewUpStationCase(section));
    }

    private static boolean existAnyNewDownStation(final List<Section> sections, final Section section) {
        return sections.stream()
            .anyMatch(it -> it.addNewDownStationCase(section));
    }


    public boolean hasMiddleSection() {
        return this == ADD_MIDDLE_FROM_UP_STATION || this == ADD_MIDDLE_FROM_DOWN_STATION;
    }
}
