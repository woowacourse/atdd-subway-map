package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Long> getStationsId() {
        List<Long> upStationsId = getUpStationsId();

        upStationsId.add(getDownStationsId().stream()
                .filter(id -> !upStationsId.contains(id))
                .findAny()
                .orElseThrow(() -> new RuntimeException()));

        return upStationsId;
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


    public Sections update(Section section) {
        validateAddable(section);

        Optional<Section> findOverlappingSection = sections.stream()
                .filter(s -> s.isEqualOfUpStation(section) || s.isEqualOfDownStation(section))
                .findAny();

        if (findOverlappingSection.isPresent()) {
            Section overlappingSection = findOverlappingSection.get();
            sections.add(overlappingSection.getCutDistanceSection(section));
            sections.remove(overlappingSection);
        }
        sections.add(section);
        return new Sections(sections);
    }

    private void validateAddable(Section section) {
        List<Long> stationsId = getStationsId();

        boolean upStationIsInclude = section.beIncludedInDownStation(stationsId);
        boolean downStationIsInclude = section.beIncludedInUpStation(stationsId);
        if (!upStationIsInclude && !downStationIsInclude) {
            throw new IllegalArgumentException("상행선과 하행선이 노선에 없습니다.");
        }
        if (upStationIsInclude && downStationIsInclude) {
            throw new IllegalArgumentException("상행선과 하행선 둘 다 노선에 이미 존재합니다.");
        }
    }

    public List<Section> value() {
        return new ArrayList<>(sections);
    }
}
