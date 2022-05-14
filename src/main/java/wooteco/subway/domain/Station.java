package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

public class Station {

    private Long id;
    private String name;

    public Station() {
    }

    public Station(Long id, String name) {
        validateNull(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(0L, name);
    }

    private void validateNull(String name) {
        if (name.isBlank()) {
            throw new ClientException("지하철 역의 이름을 입력해주세요.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return name != null ? name.equals(station.name) : station.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
