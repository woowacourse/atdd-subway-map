package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;

public class Sections {
    private static final String DUPLICATED_SECTION_ERROR_MESSAGE = "중복된 구간입니다.";
    private static final String LINK_FAILURE_ERROR_MESSAGE = "해당 구간은 역과 연결될 수 없습니다.";

    private final List<SectionWithStation> sections;

    public Sections(List<SectionWithStation> sections) {
        this.sections = sections;
    }

    public List<Station> calculateStations() {
        final List<Station> stations = new ArrayList<>();
        final SectionWithStation firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());
        final SectionWithStation lastSection = executeToLastSection(stations, firstSection);
        stations.add(lastSection.getDownStation());
        return stations;
    }

    public void validateSave(Section section) {
        checkUniqueSection(section);
        checkIsLinked(section);
    }

    private void checkUniqueSection(Section section) {
        if (hasUpStationId(section) && hasDownStationId(section)) {
            throw new IllegalArgumentException(DUPLICATED_SECTION_ERROR_MESSAGE);
        }
    }

    private void checkIsLinked(Section section) {
        if (sections.size() != 0
                && hasNoStationId(section) && hasNoStationId(section.getReverseSection())) {
            throw new IllegalArgumentException(LINK_FAILURE_ERROR_MESSAGE);
        }
    }

    private boolean hasNoStationId(Section section) {
        return !hasUpStationId(section) && !hasDownStationId(section);
    }

    private boolean hasUpStationId(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().getId().equals(inSection.getUpStationId()));
    }

    private boolean hasDownStationId(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().getId().equals(inSection.getDownStationId()));
    }

    private SectionWithStation getFirstSection() {
        return sections.stream()
                .filter(section -> isFirstUpStation(section.getUpStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("모든 상행과 하행이 연결됩니다."));
    }

    public boolean isFirstUpStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getDownStation().equals(station));
    }

    public boolean isLastDownStation(Station station) {
        return sections.stream()
                .noneMatch(section -> section.getUpStation().equals(station));
    }

    private SectionWithStation executeToLastSection(List<Station> stations, SectionWithStation nowSection) {
        while (isAnyLink(nowSection.getDownStation())) {
            stations.add(nowSection.getDownStation());
            nowSection = getNextSection(nowSection);
        }
        return nowSection;
    }

    private boolean isAnyLink(Station downStation) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().equals(downStation));
    }

    private SectionWithStation getNextSection(SectionWithStation nowSection) {
        return sections.stream()
                .filter(section -> nowSection.getDownStation().equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 하행과 상행으로 연결되는 구간이 없습니다."));
    }

    public boolean isMiddleSection(Section inSection) {
        return isMiddleUpAttachSection(inSection) || isMiddleDownAttachSection(inSection);
    }

    public boolean isMiddleUpAttachSection(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getUpStation().getId().equals(inSection.getUpStationId()));
    }

    public boolean isMiddleDownAttachSection(Section inSection) {
        return sections.stream()
                .anyMatch(section -> section.getDownStation().getId().equals(inSection.getDownStationId()));
    }
}
