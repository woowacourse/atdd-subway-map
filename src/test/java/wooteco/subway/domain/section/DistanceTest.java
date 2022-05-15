package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("지하철노선 거리")
class DistanceTest {

    private static final int DEFAULT_DISTANCE = 10;

    private Distance distance;

    @BeforeEach
    void setUp() {
        this.distance = new Distance(DEFAULT_DISTANCE);
    }

    @DisplayName("양수가 아닌 수를 저장한다.")
    @ParameterizedTest
    @ValueSource(ints = {-5, 0})
    void validateDistancePositive(int distance) {
        assertThatThrownBy(() -> new Distance(distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("거리는 양수여야 합니다.");
    }

    @DisplayName("거리를 비교한다.")
    @ParameterizedTest
    @MethodSource("provideForIsCloserThan")
    void isLessThan(Distance other, boolean expected) {
        boolean actual = distance.isLongerThan(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsCloserThan() {
        return Stream.of(
                Arguments.of(new Distance(DEFAULT_DISTANCE - 1), true),
                Arguments.of(new Distance(DEFAULT_DISTANCE), false),
                Arguments.of(new Distance(DEFAULT_DISTANCE + 1), false));
    }

    @DisplayName("거리의 차를 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForCalculateDifferenceBetween")
    void calculateDifferenceBetween(Distance other, int expected) {
        int actual = distance.calculateDifferenceBetween(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForCalculateDifferenceBetween() {
        return Stream.of(
                Arguments.of(new Distance(DEFAULT_DISTANCE - 5), 5),
                Arguments.of(new Distance(DEFAULT_DISTANCE + 6), 6));
    }

    @DisplayName("거리의 합을 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForCalculateSumBetween")
    void calculateSumBetween(Distance other, int expected) {
        int actual = distance.calculateSumBetween(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForCalculateSumBetween() {
        return Stream.of(
                Arguments.of(new Distance(5), 5 + DEFAULT_DISTANCE),
                Arguments.of(new Distance(2), 2 + DEFAULT_DISTANCE)
        );
    }

    @DisplayName("거리가 동일한지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideForEquals")
    void equals(Distance other, boolean expected) {
        boolean actual = distance.equals(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForEquals() {
        return Stream.of(
                Arguments.of(new Distance(DEFAULT_DISTANCE), true),
                Arguments.of(new Distance(DEFAULT_DISTANCE + 1), false));
    }

    @DisplayName("거리를 반환한다.")
    @Test
    void getDistance() {
        int actual = distance.getDistance();
        assertThat(actual).isEqualTo(DEFAULT_DISTANCE);
    }
}
