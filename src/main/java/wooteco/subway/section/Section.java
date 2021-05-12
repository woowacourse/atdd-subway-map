package wooteco.subway.section;

public class Section {
    private Long id;
    private Long lineId;
    private Long front;
    private Long back;
    private int distance;

    public Section(final Long id, final Long lineId, final Long front, final Long back, final int distance) {
        this.id = id;
        this.lineId = lineId;
        this.front = front;
        this.back = back;
        this.distance = distance;
    }

    public Long front() {
        return front;
    }

    public Long back() {
        return back;
    }

    public int distance() {
        return distance;
    }

    public Long id() {
        return id;
    }
}
