package wooteco.subway.web.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String color;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @Positive
    private int distance;

    public static LineRequest create(String name, String color, Long upStationId,
        Long downStationId, int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }
}