package wooteco.subway.line.domain;

public class Section {

    public static final String ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE = "구간의 길이가 기존 구간 길이보다 크거나 같을 수 없습니다.";

    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Section(final Long upStationId, final Long downStationId, final int distance) {
        validateCreateSection(upStationId, downStationId, distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Section updateWhenAdd(Section newSection) {
        int distance = getDistance() - newSection.getDistance();

        if (distance <= 0) {
            throw new IllegalArgumentException(ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE);
        }

        if (newSection.getUpStationId().equals(upStationId)) {
            return new Section(newSection.getDownStationId(), downStationId, distance);
        }

        if (newSection.getDownStationId().equals(downStationId)) {
            return new Section(upStationId, newSection.getUpStationId(), distance);
        }

        throw new IllegalArgumentException("새로운 구간과 접점이 없습니다.");
    }

    private void validateCreateSection(Long upStationId, Long downStationId, int distance) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("구간의 상행, 하행은 다른 역이어야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이가 0과 같거나 작습니다.");
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
}
