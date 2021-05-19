package wooteco.subway.line.state;

import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.util.List;

public interface State {
    State addSection(Section targetSection);

    Sections sections();

    State deleteStation(Station station);

    List<Section> sortedSections();

    boolean existSection(Station upStation, Station downStation);

    boolean noContainStation(Station upStation, Station downStation);

    int size();
}
