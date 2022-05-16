package wooteco.subway.domain.station;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Station {
    private Long id;
    private final String name;

    public Station(String name) {
        this(null, name);
    }
}
