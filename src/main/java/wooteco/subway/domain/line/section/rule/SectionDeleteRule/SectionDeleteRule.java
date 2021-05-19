package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import wooteco.subway.domain.line.section.Section;

import java.util.List;

public interface SectionDeleteRule {

    boolean isSatisfiedBy(List<Section> sections, Long stationId);
    void execute(List<Section> sections, Long stationId);

}
