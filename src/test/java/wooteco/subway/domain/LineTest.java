package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {
    private final String standardName = "신분당선";
    private final String standardColor = "bg-red-600";

    @Test
    @DisplayName("지하철 노선 이름이 공백인 경우, IllegalArgumentException이 발생한다.")
    void line_name_blank() {
        assertThatThrownBy(() -> new Line("", standardColor))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철 노선 이름이 20글자 초과인 경우, IllegalArgumentException이 발생한다.")
    void line_name_oversize() {
        String oversizeName = "2호선".repeat(20);

        assertThatThrownBy(() -> new Line(oversizeName, standardColor))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철 노선 색상이 공백인 경우, IllegalArgumentException이 발생한다.")
    void line_color_blank() {
        assertThatThrownBy(() -> new Line(standardName, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("지하철 노선 색상이 30글자 초과인 경우, IllegalArgumentException이 발생한다.")
    void line_color_oversize() {
        String oversizeColor = "green".repeat(30);

        assertThatThrownBy(() -> new Line(standardName, oversizeColor))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선에 포함된 역을 삭제한다.")
    void deleteStation() {
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        List<Station> stations = new ArrayList<>(Arrays.asList(station1, station2, station3));
        Line line = new Line(1L, standardName, standardColor, stations);

        line.deleteStation(station1);

        assertFalse(line.containsStation(station1));
    }

    @Test
    @DisplayName("역이 노선에 포함되는 경우 true를 반환한다.")
    void containsStation() {
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Line line = new Line(1L, standardName, standardColor, List.of(station1, station2));

        assertTrue(line.containsStation(station1));
    }

    @Test
    @DisplayName("노선에 존재하는 구간이 한개인 경우 true를 반환한다.")
    void isDefaultSection() {
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Line line = new Line(1L, standardName, standardColor, List.of(station1, station2));

        assertTrue(line.isDefaultSection());
    }
}
