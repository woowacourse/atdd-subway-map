package wooteco.subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> getStationsId() {
        List<Long> upStationsId = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        upStationsId.add(sections.stream()
                .map(Section::getDownStationId)
                .filter(id -> !upStationsId.contains(id))
                .findAny()
                .orElseThrow(() -> new RuntimeException()));

        return upStationsId;
    }
}
