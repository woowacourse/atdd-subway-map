package wooteco.subway.domain;

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

    public boolean hasSameName(Station station){
        return name.equals(station.getName());
    }
}
