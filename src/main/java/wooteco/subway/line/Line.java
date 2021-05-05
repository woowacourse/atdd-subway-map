package wooteco.subway.line;

import wooteco.subway.station.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Line {
    private final String name;
    private final List<Station> stations;

    public Line(String name, Station start, Station end) {
        validateDuplicateStation(start, end);
        this.name = name;
        this.stations = new ArrayList<>(Arrays.asList(start, end));
    }

    private void validateDuplicateStation(Station start, Station end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("같은 역을 상행, 하행 종착역으로 입력하였습니다.");
        }
    }
    
}
