package wooteco.subway.domain;

public class Station {
    private Long id;
    private final String name;

    public Station(Long id, String name) {
        validateNameSize(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        validateNameSize(name);
        this.name = name;
    }

    private void validateNameSize(String name) {
        if (name == null || name.isBlank() || name.length() > 255) {
            throw new IllegalArgumentException("존재할 수 없는 이름입니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

