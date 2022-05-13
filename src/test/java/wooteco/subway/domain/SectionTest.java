package wooteco.subway.domain;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("상행역을 기준으로 구간을 분리한다.")
    void splitByUpStation() {
        Line line = new Line("2호선", "green");
        Station 삼성 = new Station(1L, "삼성");
        Station 창동 = new Station(2L, "창동");
        Station 강남 = new Station(3L, "강남");

        Section section1 = new Section(line, 삼성, 창동, 10);
        Section section2 = new Section(line, 삼성, 강남, 4);

        List<Section> sections = section1.splitFromUpStation(section2);
    }

}
