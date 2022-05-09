package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import wooteco.subway.utils.exception.NotValidSectionCreateException;

public class SectionTest {

    @Test
    void distanceLessOrEqualsThanZeroException() {
        assertThatThrownBy(() ->
                new Section(1L, new Station(1L, "신당역"),
                        new Station(2L, "동묘앞역"), 0))
                .isInstanceOf(NotValidSectionCreateException.class);
    }
}
