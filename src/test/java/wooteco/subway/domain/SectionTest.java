package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @Test
    @DisplayName("상행 종점과 하행 종점이 같으면 예외를 반환한다.")
    void checkWhetherStationsAreDifferent() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "강남역", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행 종점과 하행 종점이 같을 수 없습니다.");
    }

    @Test
    @DisplayName("두 종점간의 거리가 0보다 작거나 같으면 예외를 반환한다.")
    void checkValidDistance_zero() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "선릉역", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }

    @Test
    @DisplayName("두 종점간의 거리가 0보다 작거나 같으면 예외를 반환한다.")
    void checkValidDistance_negative() {
        assertThatThrownBy(() -> new Section(1L, "강남역", "선릉역", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 종점간의 거리는 0보다 커야합니다.");
    }
}

