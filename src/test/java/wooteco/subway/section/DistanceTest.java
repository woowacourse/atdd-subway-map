package wooteco.subway.section;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @Test
    void createTest() {
        //given
        int value = 1;

        //when
        Distance distance = new Distance(1);

        //then
        assertThat(distance).isInstanceOf(Distance.class);
        assertThat(distance).isEqualTo(new Distance(1));
    }

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