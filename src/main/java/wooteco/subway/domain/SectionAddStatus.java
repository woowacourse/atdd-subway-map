package wooteco.subway.domain;

import java.util.Objects;

public enum SectionAddStatus {

    ADD_MIDDLE_FROM_UP_STATION,
    ADD_MIDDLE_FROM_DOWN_STATION,
    ADD_NEW_UP_STATION, ADD_NEW_DOWN_STATION;

    private static final String ERROR_INVALID_STATIONS = "[ERROR] 구간을 추가하기 위해선 상행 혹은 하행 종점 둘 중 하나만 포함해야 합니다.";

    public static SectionAddStatus from(final Section section,
                                        final Long upStationId,
                                        final Long downStationId) {
        if (isOnlyUpStationSame(section, upStationId, downStationId)) {
            // TODO 거리 검증 필요
            return ADD_MIDDLE_FROM_UP_STATION;
        }
        if (isOnlyDownStationSame(section, upStationId, downStationId)) {
            // TODO 거리 검증 필요
            return ADD_MIDDLE_FROM_DOWN_STATION;
        }
        if (Objects.equals(section.getDownStationId(), upStationId)) {
            return ADD_NEW_UP_STATION;
        }
        if (Objects.equals(section.getUpStationId(), downStationId)) {
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

    public boolean getAddMiddleFromUpStation() {
        return this == ADD_MIDDLE_FROM_UP_STATION;
    }
}
