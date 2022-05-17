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
    @MethodSource("provideForIsLongerThan")
    void isLongerThan(Distance other, boolean expected) {
        boolean actual = distance.isLongerThan(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForIsLongerThan() {
        return Stream.of(
                Arguments.of(new Distance(DEFAULT_DISTANCE - 1), true),
                Arguments.of(new Distance(DEFAULT_DISTANCE), false),
                Arguments.of(new Distance(DEFAULT_DISTANCE + 1), false));
    }

    @DisplayName("거리의 차를 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForSubtract")
    void subtract(Distance other, int expected) {
        int actual = distance.subtract(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForSubtract() {
        return Stream.of(
                Arguments.of(new Distance(DEFAULT_DISTANCE - 5), 5),
                Arguments.of(new Distance(DEFAULT_DISTANCE + 6), 6));
    }

    @DisplayName("거리의 합을 계산한다.")
    @ParameterizedTest
    @MethodSource("provideForSum")
    void sum(Distance other, int expected) {
        int actual = distance.sum(other);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> provideForSum() {
        return Stream.of(
                Arguments.of(new Distance(5), 5 + DEFAULT_DISTANCE),
                Arguments.of(new Distance(2), 2 + DEFAULT_DISTANCE)
        );
    }

    @DisplayName("거리를 반환한다.")
    @Test
    void getDistance() {
        int actual = distance.getDistance();
        assertThat(actual).isEqualTo(DEFAULT_DISTANCE);
    }
}
