package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import wooteco.subway.domain.line.section.Section;

import java.util.List;

public class UpTerminalDelete implements SectionDeleteRule {

    @Override
    public boolean isSatisfiedBy(List<Section> sections, Long stationId) {
        return new MatchedSection(sections, stationId).isUpStationDelete();
    }

    @Override
    public void execute(List<Section> sections, Long stationId) {
            sections.remove(new MatchedSection(sections, stationId).getSectionWithStationIdAsUp());
    }

}
