package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @DisplayName("기존의 구간이 새로운 길이보다 작거나 같으면 true를 반환한다.")
    @Test
    void isEqualsAndSmallerThan() {
        Section section = new Section(1L, new Station("합정역"), new Station("홍대입구역"), 10);
        assertThat(section.isEqualsAndSmallerThan(11)).isTrue();
    }
}
