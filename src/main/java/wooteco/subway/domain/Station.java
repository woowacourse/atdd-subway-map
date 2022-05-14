package wooteco.subway.domain;

import java.util.List;
import java.util.Objects;

public class Station {

    private final Long id;
    private final Name name;

    public Station(final Long id, final String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public static Station createWithoutId(final String name) {
        return new Station(null, name);
    }

    public boolean isResistedStation(final List<Section> sections) {
        return sections.stream()
                .anyMatch(section ->
                        section.getDownStation().equals(this)
                                || section.getUpStation().equals(this)
                );
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
        if (!(o instanceof Station)) {
            return false;
        }
        final Station station = (Station) o;
        return Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

