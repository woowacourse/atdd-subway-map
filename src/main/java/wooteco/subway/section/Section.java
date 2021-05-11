package wooteco.subway.section;

public class Section {
    private Long lineId;
    private Long front;
    private Long back;
    private int distance;

    public Section(Long lineId, Long front, Long back, int distance) {
        this.lineId = lineId;
        this.front = front;
        this.back = back;
        this.distance = distance;
    }

    public Section(Long front, Long back, int distance) {
        this.front = front;
        this.back = back;
        this.distance = distance;
    }

    public Section(Long front, Long back) {
        this.front = front;
        this.back = back;
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

    public Long lineId() {
        return lineId;
    }
}
