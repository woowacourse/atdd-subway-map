package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    private final List<Section> values;

    public Sections(final List<Section> values) {
        this.values = values;
    }

    public List<Long> getStationIds() {
        List<Long> ids = new ArrayList<>();
        for (Section section : values) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return ids.stream()
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }
}
