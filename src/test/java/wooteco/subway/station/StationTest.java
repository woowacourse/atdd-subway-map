package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotInputDataException;

@DisplayName("station 객체 테스트")
class StationTest {

    @Test
    @DisplayName("입력을 null 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataNull() {
        assertThatThrownBy(() -> new Station(0L, null))
            .isInstanceOf(NotInputDataException.class);
    }

    @Test
    @DisplayName("입력을 공백 상태로 객체를 만들면 에러가 발생한다.")
    public void createWithDataSpace() {
        assertThatThrownBy(() -> new Station(0L, " "))
            .isInstanceOf(NotInputDataException.class);
    }
}