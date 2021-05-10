package wooteco.subway.dao.fixture;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

public class Fixture {
    public static Line makeLine(String color, String name) {
        return new Line(color, name);
    }

    public static Station makeStation(Long id, String name) {
        return new Station(id, name);
    }

    public static Station makeStation(String name) {
        return new Station(name);
    }
}
