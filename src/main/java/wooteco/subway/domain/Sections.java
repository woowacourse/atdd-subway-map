package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final List<Section> values = new LinkedList<>();

    public Sections(Section section) {
        values.add(section);
    }

    public void add(Section section) {
        if (getUpDestination().equals(section.getDownStation())) {
            values.add(0, section);
        }
    }

    public Station getUpDestination() {
        return values.get(0).getUpStation();
    }
}
