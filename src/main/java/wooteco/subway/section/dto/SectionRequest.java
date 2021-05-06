package wooteco.subway.section.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;
//    private int extraFare;

    public SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
//        this.extraFare = extraFare;
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

//    public int getExtraFare() {
//        return extraFare;
//    }
}
