package wooteco.subway.ui.dto;

import wooteco.subway.domain.Section;

public class SectionRequest {

    private static final String INVALID_DISTANCE_ERROR_MESSAGE = "유효하지 않은 거리입니다.";
    private static final String DUPLICATED_SECTIONS_ERROR_MESSAGE = "상행과 하행은 같은 역으로 등록할 수 없습니다.";
    private static final int MIN_DISTANCE = 1;

    private Long upStationId;
    private Long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        validSection(upStationId, downStationId, distance);
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static SectionRequest from(LineCreateRequest lineCreateRequest) {
        return new SectionRequest(lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId(),
                lineCreateRequest.getDistance());
    }

    private void validSection(Long upStationId, Long downStationId, int distance) {
        validDistance(distance);
        validStations(upStationId, downStationId);
    }

    private void validDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            System.out.println(distance);
            System.out.println(MIN_DISTANCE);
            throw new IllegalArgumentException(INVALID_DISTANCE_ERROR_MESSAGE);
        }
    }

    private void validStations(Long upStationId, Long downStationId) {
        if (downStationId.equals(upStationId)) {
            throw new IllegalArgumentException(DUPLICATED_SECTIONS_ERROR_MESSAGE);
        }
    }

    public Section toEntity(Long lineId) {
        return new Section(0L, lineId, upStationId, downStationId, distance);
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
