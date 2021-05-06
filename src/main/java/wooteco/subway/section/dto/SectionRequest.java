package wooteco.subway.section.dto;

import javax.validation.constraints.NotNull;

public class SectionRequest {
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private int distance;
//    private int extraFare;

    private SectionRequest() {
    }

    private SectionRequest(Long upStationId, Long downStationId, int distance) {
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
