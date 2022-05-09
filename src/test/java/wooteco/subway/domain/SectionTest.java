package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.utils.exception.SectionCreateException;

public class SectionTest {

    @DisplayName("거리가 0과 같거나 그 이하일경우 예외가 발생한다.")
    @Test
    void distanceLessOrEqualsThanZeroException() {
        assertThatThrownBy(() ->
                new Section(1L, new Station(1L, "신당역"),
                        new Station(2L, "동묘앞역"), 0))
                .isInstanceOf(SectionCreateException.class);
    }
}
