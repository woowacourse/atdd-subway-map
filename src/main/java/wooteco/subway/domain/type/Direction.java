package wooteco.subway.domain.type;

public enum Direction {
    UP, DOWN;


    public Direction getReversed() {
        if (this == UP) {
            return DOWN;
        }
        return UP;
    }
}
