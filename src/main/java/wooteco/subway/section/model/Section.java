package wooteco.subway.section.model;

import wooteco.subway.line.model.Line;
import wooteco.subway.station.model.Station;

import java.util.List;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private Line line;
    private int distance;
}
