package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("두개의 색션을 합칠 수 있다.")
    void merge() {
        Section section1 = new Section(2L, 1L, 2L, 5);
        Section section2 = new Section(2L, 2L, 3L, 5);
        Section merge = section1.merge(section2);
        Assertions.assertAll(
                () -> assertThat(merge.getUpStationId()).isEqualTo(1L),
                () -> assertThat(merge.getDownStationId()).isEqualTo(3L),
                () -> assertThat(merge.getDistance()).isEqualTo(10)
        );
    }
}
