package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("거리를 비교한다.")
    @Test
    void isCloserThan() {
        Distance thisDistance = new Distance(10);
        Distance otherDistance = new Distance(5);
        assertThat(thisDistance.isCloserThan(otherDistance)).isFalse();
    }

    @DisplayName("양수가 아닌 수를 저장한다.")
    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    void validateDistancePositive(int distance) {
        assertThatThrownBy(() -> new Distance(distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 양수여야 합니다.");
    }

    @DisplayName("거리 간의 차이를 계산한다.")
    @Test
    void calculateDifferenceBetween() {
        Distance thisDistance = new Distance(10);
        Distance otherDistance = new Distance(5);

        int differenceBetweenDistances = thisDistance.calculateDifferenceBetween(otherDistance);
        assertThat(differenceBetweenDistances).isEqualTo(5);
    }

    @DisplayName("거리가 동일한지 확인한다.")
    @ParameterizedTest
    @CsvSource(value = {"5,5,true", "5,1,false"})
    void equals(int thisLength, int otherLength, boolean expected) {
        Distance thisDistance = new Distance(thisLength);
        Distance otherDistance = new Distance(otherLength);

        boolean actual = thisDistance.equals(otherDistance);
        assertThat(actual).isEqualTo(expected);
    }
}