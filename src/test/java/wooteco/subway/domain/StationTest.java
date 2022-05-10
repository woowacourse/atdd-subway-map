package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("지하철 역 이름이 공백인 경우, IllegalArgumentException이 발생한다.")
    void line_name_blank() {
        assertThatThrownBy(() -> new Station(""))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("지하철 역 이름이 20글자 초과인 경우, IllegalArgumentException이 발생한다.")
    void line_name_oversize() {
        String oversizeName = "지하철역이름".repeat(20);

        assertThatThrownBy(() -> new Station((oversizeName)))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
