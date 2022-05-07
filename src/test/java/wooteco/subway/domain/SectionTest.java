package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    @DisplayName("동일한 이름의 역으로 구간 생성 시 예외 발생")
    @Test
    void 동일_역으로_구간_생성_예외발생() {
        Station up = new Station("선릉역");
        Station down = new Station("선릉역");

        assertThatThrownBy(() -> new Section(up, down, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("유효하지 않은 길이로 구간 생성 시 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "0.8"})
    void 자연수가_아닌_길이로_구간_생성_예외발생(String distance) {
        Station up = new Station("선릉역");
        Station down = new Station("잠실역");

        assertThatThrownBy(() -> new Section(up, down, Integer.parseInt(distance)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}