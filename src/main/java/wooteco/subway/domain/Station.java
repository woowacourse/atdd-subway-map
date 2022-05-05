package wooteco.subway.domain;

public class Station {
    private Long id;
    private String name;

    public Station(Long id, String name) {
        validateNotNull(name, "name");
        this.id = id;
        this.name = name;
    }

    private void validateNotNull(String input, String param) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(String.format("%s은 필수 입력값입니다.", param));
        }
    }

    public Station(String name) {
        this(null, name);
    }

    public Station() {
    }

    public boolean hasSameNameWith(Station otherStation) {
        return this.name.equals(otherStation.name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

