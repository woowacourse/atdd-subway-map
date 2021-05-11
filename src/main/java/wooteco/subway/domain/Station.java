package wooteco.subway.domain;

import wooteco.subway.exception.SubwayIllegalArgumentException;

import java.util.Objects;

public class Station {
    private final Long id;
    private final String name;

    private Station(Long id, String name) {
        validateArguments(name);
        this.id = id;
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(null, name);
    }

    public static Station of(Long id, String name) {
        return new Station(id, name);
    }

    private void validateArguments(String name) {
        Objects.requireNonNull(name);
        if(name.length() < 1){
            throw new SubwayIllegalArgumentException("역 이름은 1글자 이상이어야합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}

