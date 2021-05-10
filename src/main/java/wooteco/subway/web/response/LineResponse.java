package wooteco.subway.web.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wooteco.subway.domain.Line;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public static LineResponse create(Line line) {
        final List<StationResponse> stationResponses =
            line.stations().stream()
                .map(StationResponse::create).collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }
}
