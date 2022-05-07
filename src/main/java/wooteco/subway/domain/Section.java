package wooteco.subway.domain;

public class Section {

    private final Station up;
    private final Station down;
    private final int distance;

    public Section(Station up, Station down, int distance) {
        validateStationsNotEqual(up, down);
        validateDistanceIsNatural(distance);
        this.up = up;
        this.down = down;
        this.distance = distance;
    }

    private void validateStationsNotEqual(Station up, Station down) {
        if (up.equals(down)) {
            throw new IllegalArgumentException("구간은 서로 다른 두 역으로 만들어야 합니다.");
        }
    }

    private void validateDistanceIsNatural(int distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리는 자연수여야 합니다.");
        }
    }

    public Station getUp() {
        return up;
    }

    public Station getDown() {
        return down;
    }

    public Relation calculateRelation(Section section) {
        if (this.up.equals(section.up) || this.down.equals(section.down)) {
            return Relation.INCLUDE;
        }
        if (this.up.equals(section.down) || this.down.equals(section.up)) {
            return Relation.EXTEND;
        }
        return Relation.NONE;
    }
}
