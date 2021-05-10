package wooteco.subway.web.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wooteco.subway.domain.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StationResponse {

    private Long id;
    private String name;

    public static StationResponse create(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}

