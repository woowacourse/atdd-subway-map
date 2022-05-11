package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getAllStationId() {
        List<Long> stationsId = new ArrayList<>();
        addUpStationsId(stationsId);
        addDownStationsId(stationsId);
        return stationsId.stream()
            .distinct()
            .collect(Collectors.toList());
    }

    private void addUpStationsId(List<Long> ids) {
        sections.forEach(section -> ids.add(section.getUpStationId()));
    }

    private void addDownStationsId(List<Long> ids) {
        sections.forEach(section -> ids.add(section.getDownStationId()));
    }
}
