package wooteco.subway.domain;

import java.util.List;

public class Sections {

    private static final int FIRST_STATION = 0;
    private static final int FIRST_STATION_INDEX = 0;

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void add(Section requestSection) {
        validateSection(requestSection);
        saveFinalSection(requestSection);
    }

    private void validateSection(Section requestSection) {
        final boolean isIncludedUpStation = sections.stream()
                .anyMatch(section -> section.getUpStationId().equals(requestSection.getUpStationId())
                        || section.getUpStationId().equals(requestSection.getDownStationId()));
        final boolean isIncludedDownStation = sections.stream()
                .anyMatch(section -> section.getDownStationId().equals(requestSection.getUpStationId())
                        || section.getDownStationId().equals(requestSection.getDownStationId()));

        if (isIncludedUpStation == true && isIncludedDownStation == true) {
            throw new IllegalArgumentException("이미 연결되어 있는 구간입니다.");
        }

        if (isIncludedUpStation == false && isIncludedDownStation == false) {
            throw new IllegalArgumentException("구간에 등록되지 않은 역입니다.");
        }
    }

    private void saveFinalSection(Section requestSection) {
        if (sections.get(FIRST_STATION).getUpStationId().equals(requestSection.getDownStationId())) {
            sections.add(FIRST_STATION_INDEX, requestSection);
            return;
        }
        sections.add(requestSection);
    }

    public List<Section> getSections() {
        return sections;
    }
}
