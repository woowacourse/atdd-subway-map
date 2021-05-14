package wooteco.subway.domain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import wooteco.subway.common.Id;
import wooteco.subway.exception.badRequest.WrongInformationException;

@Getter
public class Station {

    @Id
    private Long id;
    private String name;

    private Station(Long id, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new WrongInformationException();
        }
        this.id = id;
        this.name = name;
    }

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

