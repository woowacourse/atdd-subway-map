package wooteco.subway.domain.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import wooteco.subway.domain.station.Station;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("하나 이상의 구간이 존재해야 합니다.");
        }
        this.values = sortSectionFromUpToDown(values);
    }

    private List<Section> sortSectionFromUpToDown(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        Section topSection = findTopSection(sections);

        sortedSections.add(topSection);
        for (int i = 0; i < sections.size() - 1; i++) {
            Section lastSection = sortedSections.get(sortedSections.size() - 1);
            for (Section section : sections) {
                if (lastSection.getDownStation().equals(section.getUpStation())) {
                    sortedSections.add(section);
                    break;
                }
            }
        }

        return sortedSections;
    }

    private Section findTopSection(List<Section> sections) {
        int numOfSections = sections.size();

        for (int i = 0; i < numOfSections; i++) {
            boolean isFound = true;
            Section topSection = sections.get(i);
            for (int j = 0; j < numOfSections; j++) {
                Section temp = sections.get(j);
                if (topSection.getUpStation().equals(temp.getDownStation())) {
                    isFound = false;
                    break;
                }
            }
            if (isFound) {
                return topSection;
            }
        }
        throw new IllegalArgumentException("상행 종점 구간을 찾을 수 없습니다.");
    }

    public List<Section> getValues() {
        return Collections.unmodifiableList(values);
    }
}
