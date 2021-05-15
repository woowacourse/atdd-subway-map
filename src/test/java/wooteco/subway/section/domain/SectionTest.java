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
        Distance expectedDistance = new Distance(100);

        // when
        Section section = new Section(expectedId, expectedUpStationId, expectedDownStationId, expectedDistance);

        // then
        assertThat(section).isInstanceOf(Section.class);
    }

    @DisplayName("구간의 upStationId가 같은지 비교한다.")
    @Test
    void isSameUpStationId() {
        //given
        Section 기준_구간 = new Section(1L, 1L, 2L, new Distance(10));
        Section 기준_구간과_upStationId가_같은_구간 = new Section(1L, 1L, 3L, new Distance(20));
        Section 기준_구간과_upStationId가_다른_구간 = new Section(1L, 2L, 3L, new Distance(20));

        //when
        assertThat(기준_구간.isSameUpStationId(기준_구간과_upStationId가_같은_구간)).isTrue();
        assertThat(기준_구간.isSameUpStationId(기준_구간과_upStationId가_다른_구간)).isFalse();
    }
}