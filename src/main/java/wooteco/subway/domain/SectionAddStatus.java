package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public enum SectionAddStatus {

    ADD_MIDDLE_FROM_UP_STATION,
    ADD_MIDDLE_FROM_DOWN_STATION,
    ADD_NEW_UP_STATION,
    ADD_NEW_DOWN_STATION;

    private static final String ERROR_INVALID_STATIONS = "[ERROR] 구간을 추가하기 위해선 상행 혹은 하행 종점 둘 중 하나만 포함해야 합니다.";

    public static SectionAddStatus from(final List<Section> sections, final Section section) {
        if (sections.stream()
            .anyMatch(it -> isOnlyUpStationSame(section, it.getUpStationId(), it.getDownStationId()))) {
            return ADD_MIDDLE_FROM_UP_STATION;
        }
        if (sections.stream()
            .anyMatch(it -> isOnlyDownStationSame(section, it.getUpStationId(), it.getDownStationId()))) {
            return ADD_MIDDLE_FROM_DOWN_STATION;
        }
        if (sections.stream()
            .anyMatch(it -> addNewUpStationCase(section, it.getUpStationId()))) {
            return ADD_NEW_UP_STATION;
        }
        return ADD_NEW_DOWN_STATION;
    }

    public static SectionAddStatus from(final Section section,
                                        final Long upStationId,
                                        final Long downStationId) {
        if (isOnlyUpStationSame(section, upStationId, downStationId)) {
            return ADD_MIDDLE_FROM_UP_STATION;
        }
        if (isOnlyDownStationSame(section, upStationId, downStationId)) {
            return ADD_MIDDLE_FROM_DOWN_STATION;
        }
        if (addNewUpStationCase(section, upStationId)) {
            return ADD_NEW_UP_STATION;
        }
        if (addNewDownStationCase(section, downStationId)) {
            return ADD_NEW_DOWN_STATION;
        }
        throw new IllegalStateException(ERROR_INVALID_STATIONS);
    }

    private static boolean isOnlyUpStationSame(final Section section, final Long upStationId,
                                               final Long downStationId) {
        return Objects.equals(section.getUpStationId(), upStationId) &&
            !Objects.equals(section.getDownStationId(), downStationId);
    }

    private static boolean isOnlyDownStationSame(final Section section, final Long upStationId,
                                                 final Long downStationId) {
        return !Objects.equals(section.getUpStationId(), upStationId) &&
            Objects.equals(section.getDownStationId(), downStationId);
    }

    private static boolean addNewUpStationCase(final Section section, final Long upStationId) {
        return Objects.equals(section.getDownStationId(), upStationId);
    }

    private static boolean addNewDownStationCase(final Section section, final Long downStationId) {
        return Objects.equals(section.getUpStationId(), downStationId);
    }
}
