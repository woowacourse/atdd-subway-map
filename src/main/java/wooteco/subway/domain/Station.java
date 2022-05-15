package wooteco.subway.domain;

public class Station {
    private static final int NAME_MAX_LENGTH = 255;

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        validateName(name);

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

    public boolean isSameName(Station station) {
        return this.name.equals(station.name);
    }

    private void validateName(String name) {
        if (name.isBlank() || name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("역 이름의 길이는 1 이상 " + NAME_MAX_LENGTH + " 이하여야 합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Station station = (Station) o;

        return id != null ? id.equals(station.id) : station.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

