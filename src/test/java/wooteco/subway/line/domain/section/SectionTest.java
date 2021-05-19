package wooteco.subway.line.domain.section;

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

    @DisplayName("구간의 상행역 ID 가 같은지 비교한다.")
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

    @DisplayName("구간의 하행역 ID 가 같은지 비교한다.")
    @Test
    void isSameDownStationId() {
        //given
        Section 기준_구간 = new Section(1L, 1L, 2L, new Distance(10));
        Section 기준_구간과_downStationId가_같은_구간 = new Section(1L, 1L, 2L, new Distance(20));
        Section 기준_구간과_downStationId가_다른_구간 = new Section(1L, 2L, 3L, new Distance(20));

        //when
        assertThat(기준_구간.isSameUpStationId(기준_구간과_downStationId가_같은_구간)).isTrue();
        assertThat(기준_구간.isSameUpStationId(기준_구간과_downStationId가_다른_구간)).isFalse();
    }

    @DisplayName("구간이 상행역 또는 하행역을 가지고 있는지 확인한다.")
    @Test
    void hasUpStationIdOrDownStationId() {
        //given
        Section 기준_구간 = new Section(1L, 1L, 2L, new Distance(10));
        Section 상행역_ID가_같은_구간 = new Section(1L, 1L, 3L, new Distance(5));
        Section 하행역_ID가_같은_구간 = new Section(1L, 3L, 2L, new Distance(5));

        //when
        boolean 참이_나와야할_결과 = 기준_구간.hasUpStationIdOrDownStationId(상행역_ID가_같은_구간);
        boolean 참이_나와야할_결과2 = 기준_구간.hasUpStationIdOrDownStationId(하행역_ID가_같은_구간);

        //then
        assertThat(참이_나와야할_결과).isTrue();
        assertThat(참이_나와야할_결과2).isTrue();
    }

    @DisplayName("인자로 준 역ID 를 포함하는지 확인한다.")
    @Test
    void hasStationId() {
        //given
        Long stationId = 1L;
        Section 상행선1L_하행성2L_구간 = new Section(1L, 1L, 2L, new Distance(10));
        Section 상행선2L_하행성3L_구간 = new Section(1L, 2L, 3L, new Distance(20));

        //when
        boolean 참이_나와야할_결과 = 상행선1L_하행성2L_구간.hasStationId(stationId);
        boolean 거짓이_나와야할_결과 = 상행선2L_하행성3L_구간.hasStationId(stationId);

        //then
        assertThat(참이_나와야할_결과).isTrue();
        assertThat(거짓이_나와야할_결과).isFalse();
    }
}