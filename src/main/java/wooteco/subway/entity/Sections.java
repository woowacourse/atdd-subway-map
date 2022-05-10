package wooteco.subway.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sections {

    private final List<SectionViewEntity> value;

    public Sections(List<SectionViewEntity> value) {
        this.value = value;
    }

    public List<StationEntity> getStations() {
        Set<StationEntity> stations = new HashSet<>();
        for (SectionViewEntity section : value) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    @Override
    public String toString() {
        return "Sections{" + "value=" + value + '}';
    }
}
