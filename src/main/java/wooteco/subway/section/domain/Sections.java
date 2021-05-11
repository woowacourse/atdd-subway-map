package wooteco.subway.section.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> getStationsId() {
        List<Long> stationsId = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());

        stationsId.add(getLastSection().getDownStationId());

        return stationsId;
    }

    private Section getLastSection() {
        return sections.get(sections.size()-1);
    }
}
