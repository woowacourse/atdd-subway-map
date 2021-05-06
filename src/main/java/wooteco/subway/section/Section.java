package wooteco.subway.section;

public class Section {

    private Long id;
    private String upStationId;
    private String downStationId;
    private Long distance;


    public Section() {
    }

    public Section(Long id, String upStationId, String downStationId, Long distance) {
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(String upStationId, String downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }
}
