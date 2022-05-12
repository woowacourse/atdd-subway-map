package wooteco.subway.domain;

public class Station implements Comparable<Station> {

    private Long id;
    private Name name;

    public Station(final String name) {
        this(null, name);
    }

    public Station(final Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Station station = (Station) o;

        if (getId() != null ? !getId().equals(station.getId()) : station.getId() != null) {
            return false;
        }
        return getName() != null ? getName().equals(station.getName()) : station.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }

    @Override
    public int compareTo(final Station otherStation) {
        return Long.compare(this.id, otherStation.id);
    }

}

