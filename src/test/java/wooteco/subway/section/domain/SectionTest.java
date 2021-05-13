package wooteco.subway.section.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SectionTest {

    @DisplayName("Section 인스턴스 생성된다.")
    @Test
    void create() {
        // given
        Long expectedId = 1L;
        Long expectedUpStationId = 1L;
        Long expectedDownStationId = 10L;
        Distance excpectedDistance = new Distance(100);

        // when
        Section section = new Section(expectedId, expectedUpStationId, expectedDownStationId, excpectedDistance);

        // then
        assertThat(section).isInstanceOf(Section.class);
    }
}