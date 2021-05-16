package wooteco.subway.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import wooteco.subway.exception.InsufficientStationInformationException;

@Getter
@NoArgsConstructor
public class Station {
    private Long id;
    private String name;

    private Station(Long id, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new InsufficientStationInformationException();
        }

        this.id = id;
        this.name = name;
    }

    public static Station from(String name) {
        return of(null, name);
    }

    public static Station of(Long id, String name) {
        return new Station(id, name);
    }

    public boolean isSameId(Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }
}
