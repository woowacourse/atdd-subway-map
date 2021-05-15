package wooteco.subway.line.domain;

import wooteco.subway.line.domain.rule.FindSectionRule;

import java.util.Collections;
import java.util.List;

public class Line {
    private final Long id;
    private final String name;
    private final String color;
    private final Sections sections;

    public Line(final Long id, final String name, final String color, final Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(final String name, final String color, final Sections sections) {
        this(null, name, color, sections);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new Sections(Collections.emptyList()));
    }

    public void validateEnableAddSection(Section newSection) {
        sections.validateEnableAddSection(newSection);
    }

    public boolean isEndPoint(Section newSection) {
        return sections.checkEndPoint(newSection);
    }

    public Section findDeleteByAdding(Section newSection, List<FindSectionRule> findSectionRules) {
        return sections.findDeleteByAdding(newSection, findSectionRules);
    }

    public List<Section> deleteSection(final Long stationId) {
        return sections.deleteSection(stationId);
    }

    public Section generateUpdateWhenDelete(final List<Section> deleteSections) {
        return sections.generateUpdateWhenDelete(deleteSections);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }
}
