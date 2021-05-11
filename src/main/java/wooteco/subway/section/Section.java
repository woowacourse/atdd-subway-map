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

    public void set(Section section){
        this.lineId = section.lineId();
        this.front = section.front();
        this.back = section.back();
        this.distance = section.distance();
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
