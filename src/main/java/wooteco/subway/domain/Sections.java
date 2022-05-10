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
        stationsId.addAll(getUpStationsId());
        stationsId.addAll(getDownStationsId());
        return stationsId.stream()
            .distinct()
            .collect(Collectors.toList());
    }

    private List<Long> getUpStationsId() {
        return sections.stream()
            .map(Section::getUpStationId)
            .collect(Collectors.toList());
    }

    private List<Long> getDownStationsId() {
        return sections.stream()
            .map(Section::getDownStationId)
            .collect(Collectors.toList());
    }
}
