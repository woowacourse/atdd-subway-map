package wooteco.subway.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Station {
    private final Long id;
    @NotBlank(message = "역의 이름은 필수로 입력하여야 합니다.")
    private final String name;

    private Station(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Station from(String name) {
        return new Station(null, name);
    }

    public static Station of(Long id, String name) {
        return new Station(id, name);
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

