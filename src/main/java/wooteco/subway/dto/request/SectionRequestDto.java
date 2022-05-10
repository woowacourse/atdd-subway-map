package wooteco.subway.dto.request;

public class SectionRequestDto {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequestDto() {
    }

    public SectionRequestDto(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public SectionRequestDto(final LineRequestDto lineRequestDto) {
        this(lineRequestDto.getUpStationId(), lineRequestDto.getDownStationId(), lineRequestDto.getDistance());
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
}
