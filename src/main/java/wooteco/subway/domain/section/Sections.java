package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sortSections(sections);
    }

    private List<Section> sortSections(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>(sections);
        int sectionCounts = sortedSections.size();
        for (int i = 0; i < sectionCounts; i++) {
            compare(sortedSections, i);
        }
        swapEndSections(sortedSections);
        return sortedSections;
    }

    private void compare(List<Section> sections, int i) {
        int sectionCounts = sections.size();
        for (int j = i + 1; j < sectionCounts; j++) {
            swap(sections, i, j);
        }
    }

    private void swap(List<Section> sections, int i, int j) {
        Section firstSection = sections.get(i);
        Section secondSection = sections.get(j);
        if (secondSection.isConnectedWith(firstSection)) {
            Section tmp = sections.get(i + 1);
            sections.set(i, secondSection);
            sections.set(j, tmp);
            sections.set(i + 1, firstSection);
        }
    }

    private void swapEndSections(List<Section> sections) {
        int sectionCounts = sections.size();
        Section firstSection = sections.get(0);
        Section lastSection = sections.get(sectionCounts - 1);
        if (lastSection.isConnectedWith(firstSection)) {
            sections.remove(sectionCounts - 1);
            sections.add(0, lastSection);
        }
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(Section::getStations)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> toList() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    public boolean isEndStationExtension(Section section) {
        Section firstSection = sections.get(0);
        Section lastSection = sections.get(sections.size() - 1);
        return section.getDownStation().equals(firstSection.getUpStation()) || section.getUpStation().equals(lastSection.getDownStation());
    }

    public Section splitLongerSectionAfterAdding(Section section) {
        if (isNotAddableCondition(section)) {
            throw new SubwayException(ExceptionStatus.INVALID_SECTION);
        }
        Section longerSection = findTargetLongerSection(section);
        return longerSection.splitLongerSectionBy(section);
    }

    private boolean isNotAddableCondition(Section section) {
        boolean isUpStationExists = sections.stream()
                .anyMatch(section::hasSameUpStation);
        boolean isDownStationExists = sections.stream()
                .anyMatch(section::hasSameDownStation);
        return (isUpStationExists && isDownStationExists) || (!isUpStationExists && !isDownStationExists);
    }

    private Section findTargetLongerSection(Section section) {
        return sections.stream()
                .filter(targetSection -> targetSection.hasOverlappedStation(section))
                .findAny()
                .orElseThrow(() -> new SubwayException(ExceptionStatus.INVALID_SECTION));
    }
}
