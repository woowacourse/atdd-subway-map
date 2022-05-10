package wooteco.subway.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sections {

    private final List<SectionEntity> value;

    public Sections(List<SectionEntity> value) {
        this.value = value;
    }

    public List<Long> getStationIds() {
        Set<Long> stationIds = new HashSet<>();
        for (SectionEntity section : value) {
            stationIds.add(section.getUpStationId());
            stationIds.add(section.getDownStationId());
        }
        return new ArrayList<>(stationIds);
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + value + '}';
    }
}
