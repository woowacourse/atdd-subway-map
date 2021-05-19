package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import wooteco.subway.domain.line.section.Section;

import java.util.List;

public class DownTerminalDelete implements SectionDeleteRule {

    @Override
    public boolean isSatisfiedBy(List<Section> sections, Long stationId) {
        return new MatchedSection(sections, stationId).isDownStationDelete();
    }

    @Override
    public void execute(List<Section> sections, Long stationId) {
        sections.remove(new MatchedSection(sections, stationId).getSectionWithStationIdAsDown());
    }

}
