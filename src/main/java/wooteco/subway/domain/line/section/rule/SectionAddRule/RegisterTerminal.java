package wooteco.subway.domain.line.section.rule.SectionAddRule;

import wooteco.subway.domain.line.section.OrderedStationId;
import wooteco.subway.domain.line.section.Section;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public class RegisterTerminal implements SectionAddRule {

    @Override
    public boolean isSatisfiedBy(List<Section> sections, Section section) {
        Deque<Long> stationIds = new ArrayDeque<>(new OrderedStationId(sections).asList());

        return section.hasSameDownStationId(stationIds.getFirst()) ||
                section.hasSameUpStationId(stationIds.getLast());
    }

    @Override
    public void execute(List<Section> sections, Section section) {
        sections.add(section);
    }

}
