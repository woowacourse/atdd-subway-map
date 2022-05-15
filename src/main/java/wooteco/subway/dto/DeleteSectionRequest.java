package wooteco.subway.dto;

public class DeleteSectionRequest {

    private Long stationId;

    public DeleteSectionRequest() {
    }

    public DeleteSectionRequest(Long stationId) {
        this.stationId = stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getStationId() {
        return stationId;
    }
}
