package wooteco.subway.section;

public class Section {
    private Long id;
    private Long lineId;
    private Long front;
    private Long back;
    private int distance;

    public Section(Long id, Long lineId, Long front, Long back, int distance) {
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

    public Long lineId() {
        return lineId;
    }

    public Long id() {
        return id;
    }
}
