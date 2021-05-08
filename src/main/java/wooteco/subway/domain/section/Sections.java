package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<Section> toList() {
        return Collections.unmodifiableList(sections);
    }
}
