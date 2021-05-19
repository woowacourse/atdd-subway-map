package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import wooteco.subway.domain.line.section.Section;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.station.value.StationId;

import java.util.List;

public class MiddleStationDelete implements SectionDeleteRule {

    @Override
    public boolean isSatisfiedBy(List<Section> sections, Long stationId) {
        return new MatchedSection(sections, stationId).isMiddleStationsDelete();
    }

    @Override
    public void execute(List<Section> sections, Long stationId) {
        Section upSection = new MatchedSection(sections, stationId).getSectionWithStationIdAsDown();
        Section downSection = new MatchedSection(sections, stationId).getSectionWithStationIdAsUp();

        sections.remove(upSection);
        sections.remove(downSection);

        sections.add(new Section(
                new LineId(upSection.getLineId()),
                new StationId(upSection.getUpStationId()),
                new StationId(downSection.getDownStationId()),
                new Distance(upSection.getDistance() + downSection.getDistance())
        ));
    }

}
