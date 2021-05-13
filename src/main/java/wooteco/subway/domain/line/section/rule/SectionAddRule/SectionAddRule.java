package wooteco.subway.domain.line.section.rule.SectionAddRule;

import wooteco.subway.domain.line.section.Section;

import java.util.List;

public interface SectionAddRule {
    boolean isSatisfiedBy(List<Section> sections, Section section);
    void execute(List<Section> sections, Section section);
}
