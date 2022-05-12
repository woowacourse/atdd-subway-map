package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}
