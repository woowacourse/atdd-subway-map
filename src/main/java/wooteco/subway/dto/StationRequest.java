package wooteco.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import wooteco.subway.domain.station.Station;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationRequest {

    @NotNull
    private String name;

    public Station toStation() {
        return new Station(name);
    }
}
