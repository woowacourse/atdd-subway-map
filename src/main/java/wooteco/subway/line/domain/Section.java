package wooteco.subway.line.domain;

public class Section {
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

    private void validateCreateSection(Long upStationId, Long downStationId, int distance) {
        if (upStationId.equals(downStationId)) {
            throw new IllegalArgumentException("구간의 상행, 하행은 다른 역이어야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("구간의 길이가 0과 같거나 작습니다.");
        }
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
