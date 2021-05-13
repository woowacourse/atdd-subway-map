package wooteco.subway.section.domain;

import wooteco.subway.section.exception.EmptySectionsException;
import wooteco.subway.station.domain.Station;

import java.util.List;

public class EmptySections implements Sections {
    @Override
    public OrderedSections addSection(Section section) {
        throw new EmptySectionsException("비어있는 구간입니다.");
    }

    @Override
    public OrderedSections removeSection(Station station) {
        throw new EmptySectionsException("비어있는 구간입니다.");
    }

    @Override
    public List<Section> getSections() {
        throw new EmptySectionsException("비어있는 구간입니다.");
    }

    @Override
    public List<Section> getReverseSections() {
        throw new EmptySectionsException("비어있는 구간입니다.");
    }
}
