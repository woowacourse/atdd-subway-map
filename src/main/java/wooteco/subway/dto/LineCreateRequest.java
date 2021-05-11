package wooteco.subway.dto;

import wooteco.subway.exception.SubwayIllegalArgumentException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineCreateRequest {
    @NotBlank(message = "노선의 이름은 필수로 입력하여야 합니다.")
    private String name;
    @NotBlank(message = "노선의 색상은 필수로 입력하여야 합니다.")
    private String color;
    @NotNull(message = "노선의 상행역 Id는 필수로 입력하여야 합니다.")
    private Long upStationId;
    @NotNull(message = "노선의 하행역 Id는 필수로 입력하여야 합니다.")
    private Long downStationId;
    @Min(value = 1, message = "구간의 거리는 1 이상 이어야 합니다.")
    private int distance;

    private LineCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public void validateDownStationDifferFromUpStation() {
        if (upStationId.equals(downStationId)) {
            throw new SubwayIllegalArgumentException("상행과 하행 종점은 같을 수 없습니다.");
        }
    }
}
