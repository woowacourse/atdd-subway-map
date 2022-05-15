package wooteco.subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Section() {
    }

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        validateLineId(lineId);
        validateStationId(upStationId);
        validateStationId(downStationId);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, int distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
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

    private void validateLineId(Long id) {
        if (id == null) {
            throw new NullPointerException("노선 id 값이 누락되었습니다.");
        }
    }

    private void validateStationId(Long id) {
        if (id == null) {
            throw new NullPointerException("역 id 값이 누락되었습니다.");
        }
    }
}
