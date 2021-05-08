package wooteco.subway.section.domain;

public class SectionDistance {
    private static final int ZERO = 0;
    private final long distance;

    public SectionDistance(long distance) {
        whenDistanceIsNegative(distance);
        this.distance = distance;
    }

    private void whenDistanceIsNegative(long distance) {
        if (distance < ZERO) {
            throw new IllegalArgumentException(String.format("구간 거리는 음수일 수 없습니다. 입력된 거리 : %d", distance));
        }
    }

    public long getDistance() {
        return distance;
    }

    public SectionDistance sum(SectionDistance that) {
        return new SectionDistance(this.distance + that.getDistance());
    }
}
