package wooteco.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Station {
    private Long id;
    private String name;

    public static Station create(String name) {
        return create(null, name);
    }

    public static Station create(Long id, String name) {
        return new Station(id, name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}
