package wooteco.subway.domain;

import com.fasterxml.jackson.databind.exc.InvalidNullException;
import wooteco.subway.exception.IllegalSectionCreatedException;

public class Section {
    public static final int MIN_DISTANCE = 0;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        checkNull(upStationId, downStationId, distance);
        validateSameStation(upStationId, downStationId);
        validateInvalidDistance(distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void checkNull(final Long upStationId, final Long downStationId, final Integer distance) {
        if (upStationId == null || downStationId == null || distance == null) {
            throw new NullPointerException();
        }
    }

    private static void validateSameStation(final Long upStationId, final Long downStationId) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalSectionCreatedException();
        }
    }

    private static void validateInvalidDistance(final int distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalSectionCreatedException();
        }
    }

    public void validateDistanceLargerThan(final Section section) {
        if (distance <= section.distance) {
            throw new IllegalSectionCreatedException();
        }
    }

    public boolean isConnected(final Section section) {
        return downStationId.equals(section.upStationId);
    }

    public Section divideRight(final Section section) {
        return new Section(section.downStationId, downStationId, distance - section.distance);
    }

    public Section divideLeft(final Section section) {
        return new Section(upStationId, section.upStationId, distance - section.distance);
    }

    public boolean equalsUpStation(final Section section) {
        return upStationId.equals(section.upStationId);
    }

    public boolean equalsDownStation(final Section section) {
        return downStationId.equals(section.downStationId);
    }

    public boolean containsUpStationIdBy(final Section section) {
        return upStationId.equals(section.upStationId) || downStationId.equals(section.upStationId);
    }

    public boolean containsDownStationIdBy(final Section section) {
        return upStationId.equals(section.downStationId) || downStationId.equals(section.downStationId);
    }

    public Section integrate(final Section section) {
        if (downStationId.equals(section.upStationId)) {
            return new Section(upStationId, section.downStationId, distance + section.distance);
        }
        return new Section(section.upStationId, downStationId, distance + section.distance);
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
