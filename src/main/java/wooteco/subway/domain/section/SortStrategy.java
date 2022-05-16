package wooteco.subway.domain.section;

import wooteco.subway.domain.station.Station;

import java.util.List;

public interface SortStrategy {
    List<Station> sort(List<Section> sections, List<Station> stations);
}
