package wooteco.subway.line.section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sections {

    private final List<Section> sectionGroup;

    public Sections(final List<Section> sectionGroup) {
        this.sectionGroup = sectionGroup;
    }

    public List<Long> distinctStationIds() {
        final Set<Long> ids = new HashSet<>();
        for (Section section : sectionGroup) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return new ArrayList<>(ids);
    }

    public List<Section> toList() {
        return sectionGroup;
    }
}
