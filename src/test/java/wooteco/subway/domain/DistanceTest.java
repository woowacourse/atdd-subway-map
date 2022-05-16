package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.property.Distance;
import wooteco.subway.exception.InvalidRequestException;

class DistanceTest {

    @Test
    @DisplayName("양수가 아닌 수로 거리를 만들 수 없다.")
    public void throwsExceptionWithNegativeValue() {
        // given & when
        int value = 0;
        // then
        assertThatExceptionOfType(InvalidRequestException.class)
            .isThrownBy(() -> new Distance(value));
    }

    @Test
    @DisplayName("거리가 더 먼지 확인할 수 있다.")
    public void isLongerThan() {
        // given
        Distance distanceA = new Distance(10);
        Distance distanceB = new Distance(11);
        // when
        final boolean isLonger = distanceB.isLongerThan(distanceA);
        // then
        assertThat(isLonger).isTrue();
    }

    @Test
    @DisplayName("거리끼리 빼기 연산을 할 수 있다")
    public void subtract() {
        // given
        Distance distanceA = new Distance(10);
        Distance distanceB = new Distance(11);
        // when
        final Distance subtracted = distanceB.subtract(distanceA);
        // then
        assertThat(subtracted.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("거리끼리 덧셈 연산을 할 수 있다.")
    public void plus() {
        Distance distanceA = new Distance(5);
        Distance distanceB = new Distance(6);
        // when
        final Distance subtracted = distanceB.plus(distanceA);
        // then
        assertThat(subtracted.getValue()).isEqualTo(11);
    }
}