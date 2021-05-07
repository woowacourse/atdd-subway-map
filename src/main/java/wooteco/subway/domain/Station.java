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

    public static Station from(String name) {
        return new Station(null, name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }
}
