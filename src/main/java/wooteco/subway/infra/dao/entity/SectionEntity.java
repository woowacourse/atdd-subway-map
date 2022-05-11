package wooteco.subway.infra.dao.entity;

public class SectionEntity {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private String upStationName;
    private Long downStationId;
    private String downStationName;
    private int distance;

    public SectionEntity() {
    }

    public SectionEntity(Long id, Long lineId, Long upStationId, String upStationName, Long downStationId,
                         String downStationName, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.upStationName = upStationName;
        this.downStationId = downStationId;
        this.downStationName = downStationName;
        this.distance = distance;
    }

    public SectionEntity(Long id, Long lineId, Long upStationId, Long downStationId, int distance) {
        this(id, lineId, upStationId, null, downStationId, null, distance);
    }

    public SectionEntity(Long lineId, Long upStationId, Long downStationId, int distance) {
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

    public String getUpStationName() {
        return upStationName;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public String getDownStationName() {
        return downStationName;
    }

    public int getDistance() {
        return distance;
    }
}
