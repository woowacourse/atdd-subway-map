package wooteco.subway.domain;

import wooteco.subway.exception.ClientException;

public class Station {

    private static final long BASIC_ID = 0L;

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
        this(BASIC_ID, name);
    }

    private void validateNull(String name) {
        if (name.isBlank()) {
            throw new ClientException("지하철 역의 이름을 입력해주세요.");
        }
    }

    public boolean isSameName(String target) {
        return name.equals(target);
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

        if (getId() != null ? !getId().equals(station.getId()) : station.getId() != null) return false;
        return getName() != null ? getName().equals(station.getName()) : station.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
