package wooteco.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Station 테스트")
class StationTest {
    @DisplayName("역 이름이 20자가 넘어가면 예외")
    @Test
    public void whenNameOverTwentyLetters() {
        //given
        String soLongName = "나는20글자가넘는이름이다엄청나게긴이름이다대박사건";

        //when&then
        assertThatThrownBy(() -> new Station(soLongName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("역 이름은 20자를 초과할 수 없습니다.");
    }
}