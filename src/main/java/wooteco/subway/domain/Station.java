package wooteco.subway.domain;

import wooteco.subway.exception.BlankArgumentException;

public class Station {

    private final Long id;
    private final String name;

    public Station(Long id, String name) {
        if (name.isBlank()) {
            throw new BlankArgumentException("지하철의 이름은 빈 문자열일 수 없습니다.");
        }
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

