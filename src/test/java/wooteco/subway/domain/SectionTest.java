package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SectionTest {

    private final Line line = new Line(1L, "2호선", "bg-green-600");
    private final Station seolleung = new Station(1L, "선릉역");
    private final Station samseong = new Station(2L, "삼성역");

    @Test
    @DisplayName("구간 객체를 생성한다.")
    void NewSection() {
        assertThatCode(() -> new Section(line.getId(), seolleung.getId(), samseong.getId(), 10))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상행과 종점이 같으면 예외를 던진다.")
    void NewSection_SameStation_ExceptionThrown() {
        assertThatThrownBy(() -> new Section(line.getId(), seolleung.getId(), seolleung.getId(), 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점이 동일합니다.");
    }

    @ParameterizedTest
    @DisplayName("두 종점간의 거리가 유효하지 않으면 예외를 던진다.")
    @ValueSource(ints = {-1, 0})
    void NewSection_InvalidDistance_ExceptionThrown(final int distance) {
        // then
        assertThatThrownBy(() -> new Section(line.getId(), seolleung.getId(), samseong.getId(), distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리가 유효하지 않습니다.");
    }
}