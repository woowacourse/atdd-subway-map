package wooteco.subway.line.domain.rule;

import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Optional;

public class FindUpSectionStrategy implements FindSectionStrategy {
    @Override
    public Optional<Section> findSection(List<Section> sections, Station targetStation) {
        return sections.stream()
                .filter(section -> section.hasUpStation(targetStation))
                .findFirst();
    }
}
