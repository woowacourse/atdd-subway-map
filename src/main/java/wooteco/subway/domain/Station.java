package wooteco.subway.domain;

import lombok.Getter;

@Getter
public class Station {
    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}

