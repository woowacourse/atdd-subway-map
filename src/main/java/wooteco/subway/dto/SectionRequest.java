package wooteco.subway.dto;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotNull(message = "구간의 상행 아이디는 공백일 수 없습니다.")
    private Long upStationId;

    @NotNull(message = "구간의 하행 아이디는 공백일 수 없습니다.")
    private Long downStationId;

    @Positive
    @NotNull(message = "구간의 거리는 공백이거나 음수일 수 없습니다.")
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static Section toEntity(final SectionRequest sectionRequest) {
        return new Section(
                new Station(sectionRequest.getUpStationId(), ""),
                new Station(sectionRequest.getDownStationId(), ""),
                sectionRequest.getDistance()
        );
    }

    public static SectionRequest from(final Section section) {
        return new SectionRequest(
                section.getUpStation().getId(),
                section.getDownStation().getId(),
                section.getDistance()
        );
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
