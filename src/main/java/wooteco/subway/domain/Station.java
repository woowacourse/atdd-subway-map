package wooteco.subway.domain;

public class Station {

    private Long id;
    private String name;

    public Station(String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("역 이름은 공백일 수 없습니다.");
        }
    }

    public boolean isSameId(final Long id) {
        return this.id.equals(id);
    }

    public boolean isSameName(final String name) {
        return this.name.equals(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
