package wooteco.subway.line.state;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

public interface State {
    State addSection(Line line, Section targetSection);

    Sections sections();

    State deleteStation(Station station);
}
