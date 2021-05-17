package wooteco.subway.section.domain;

import wooteco.subway.station.domain.Station;

import java.util.List;

public interface Sections {
    OrderedSections addSection(Section section);

    OrderedSections removeSection(Station station);

    List<Section> getSections();

    List<Section> getReverseSections();
}
