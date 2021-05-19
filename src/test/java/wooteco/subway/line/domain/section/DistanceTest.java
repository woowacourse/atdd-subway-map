package wooteco.subway.line.domain.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.section.Distance;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceTest {

    @DisplayName("Distance 인스턴스 생성된다.")
    @Test
    void create() {
        //given
        int value = 1;

        //when
        Distance distance = new Distance(1);

        //then
        assertThat(distance).isInstanceOf(Distance.class);
        assertThat(distance).isEqualTo(new Distance(value));
    }

    @DisplayName("value() 메서드로 Distance 객체의 거리 값을 가져올 수 있다")
    @Test
    void value() {
        //given
        int expectedValue = 1;
        Distance distance = new Distance(expectedValue);

        //when
        int resultValue = distance.value();

        //then
        assertThat(resultValue).isEqualTo(expectedValue);
    }
}