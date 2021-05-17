package wooteco.subway.line.domain.rule;

import wooteco.subway.line.domain.Section;
import wooteco.subway.station.domain.Station;

import java.util.List;
import java.util.Optional;

public interface FindSectionStrategy {
    Optional<Section> findSection(List<Section> sections, Station targetStation);
}
