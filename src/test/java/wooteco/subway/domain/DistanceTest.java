package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("Distance 더하기")
    void addDistance() {
        // given
        Distance legacyDistance = new Distance(10);
        Distance addDistance = new Distance(10);

        // when
        Distance resultDistance = legacyDistance.add(addDistance);

        // then
        assertThat(resultDistance.getValue()).isEqualTo(20);
    }

    @Test
    @DisplayName("Distance 빼기")
    void subtractDistance() {
        // given
        Distance legacyDistance = new Distance(10);
        Distance subtractDistance = new Distance(9);

        // when
        Distance resultDistance = legacyDistance.subtract(subtractDistance);

        // then
        assertThat(resultDistance.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("Distance 뺀 결과가 1보다 작을 경우 예외처리")
    void subtractDistanceException() {
        // given
        Distance legacyDistance = new Distance(10);
        Distance subtractDistance = new Distance(11);

        // when

        // then
        assertThatThrownBy(() -> legacyDistance.subtract(subtractDistance))
            .isInstanceOf(InvalidDistanceException.class);
    }
}