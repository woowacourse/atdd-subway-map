package wooteco.subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @DisplayName("지하철역의 이름이 공백인지를 검사한다.")
    @Test
    public void blankNameTest() {
        // given & when & then
        Assertions.assertThatThrownBy(() -> new Station(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}