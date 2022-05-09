package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final List<Section> values = new LinkedList<>();

    public Sections(Section section) {
        values.add(section);
    }

    public void add(Section newSection) {
        if (getUpDestination().equals(newSection.getDownStation())) {
            values.add(0, newSection);
            return;
        }

        if (getDownDestination().equals(newSection.getUpStation())) {
            values.add(newSection);
            return;
        }

        for (Section section : values) {
            if (section.getUpStation().equals(newSection.getUpStation())) {
                int index = values.indexOf(section);
                values.set(index, newSection);
                values.add(index + 1, new Section(newSection.getDownStation(), section.getDownStation(),
                    section.getDistance() - newSection.getDistance()));
                return;
            }

            if (section.getDownStation().equals(newSection.getDownStation())) {
                int index = values.indexOf(section);
                values.set(index, new Section(section.getUpStation(), newSection.getUpStation(),
                    section.getDistance() - newSection.getDistance()));
                values.add(index + 1, newSection);
                return;
            }
        }
    }

    public Station getUpDestination() {
        return values.get(0).getUpStation();
    }

    public Station getDownDestination() {
        return values.get(values.size() - 1).getDownStation();
    }

    public List<Section> getValues() {
        return List.copyOf(values);
    }
}
