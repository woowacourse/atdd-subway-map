package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final Set<Section> sections;

    public Sections(Set<Section> sections) {
        this.sections = new HashSet<>(sections);
    }

    public List<Station> pathByLine(Line line) {
        Set<Section> sectionsInLine = sectionsByLine(line);
        Section now = firstSectionByLine(sectionsInLine);
        Section lastSection = lastSectionByLine(sectionsInLine);
        List<Station> result = new ArrayList<>(Collections.singletonList(now.getUpStation()));
        while (!now.equals(lastSection)) {
            result.add(now.getUpStation());
            now = nextSectionByLine(sectionsInLine, now);
        }
        result.add(now.getDownStation());
        return result;
    }

    private Set<Section> sectionsByLine(Line line) {
        return sections.stream()
            .filter(section -> section.getLine().equals(line))
            .collect(Collectors.toSet());
    }

    private Section firstSectionByLine(Set<Section> sectionsInLine) {
        return sectionsInLine.stream()
            .filter(section -> sectionsInLine.stream()
                .noneMatch(section1 -> section.getUpStation().equals(section1.getDownStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("첫 번째 구간이 존재하지 않습니다."));
    }

    private Section lastSectionByLine(Set<Section> sectionsInLine) {
        return sectionsInLine.stream()
            .filter(section -> sectionsInLine.stream()
                .noneMatch(section1 -> section.getDownStation().equals(section1.getUpStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("마지막 구간이 존재하지 않습니다."));
    }

    private Section nextSectionByLine(Set<Section> sectionsInLine, Section now) {
        return sectionsInLine.stream()
            .filter(section -> now.getDownStation().equals(section.getUpStation()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    public Construction construction(Line line) {
        return new Construction(sectionsByLine(line));
    }
}
