package wooteco.subway.station;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Station {

    private Long id;
    private String name;

    public static Station create(String name) {
        return create(null, name);
    }

    public static Station create(Long id, String name) {
        return new Station(id, name);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}

