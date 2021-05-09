package wooteco.subway.domain.section.type;

public enum Direction {
    UP, DOWN;

    public Direction getReversed() {
        if (this == UP) {
            return DOWN;
        }
        return UP;
    }
}
