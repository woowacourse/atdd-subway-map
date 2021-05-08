package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InvalidDistanceException;

class DistanceTest {

    @Test
    @DisplayName("입력된 거리가 최소 입력거리 보다 작을 경우 테스트")
    void underMinimumDistance() {
        // given

        // when

        // then
        assertThatThrownBy(() -> new Distance(0))
            .isInstanceOf(InvalidDistanceException.class);
    }
}