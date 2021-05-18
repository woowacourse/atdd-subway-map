package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.SectionHasSameUpAndDownException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[도메인] Section")
class SectionTest {
    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 잠실역 = new Station(2L, "잠실역");
    private final Station 수서역 = new Station(3L, "수서역");

    @DisplayName("상행선이 같은지 확인")
    @Test
    void isUpStation() {
        Section section1 = new Section(강남역, 잠실역, 20);

        assertTrue(section1.isUpStation(강남역));
    }

    @DisplayName("하행선이 같은지 확인")
    @Test
    void isDownStation() {
        Section section1 = new Section(강남역, 잠실역, 20);

        assertTrue(section1.isDownStation(잠실역));
    }

    @DisplayName("구간 추가로 인한 기존 구간 수정")
    @Test
    void updateByNewSection() {
        Section 강남_잠실 = new Section(강남역, 잠실역, 20);
        Section 수서_잠실 = new Section(수서역, 잠실역, 10);

        Section result = 강남_잠실.updateByNewSection(수서_잠실);

        assertThat(result).isEqualTo(new Section(강남역, 수서역, 10));
    }

    @DisplayName("생성 - 실패(상하행이 같은 역인 구간)")
    @Test
    void create() {
        assertThatThrownBy(() -> new Section(잠실역, 잠실역, 10))
                .isInstanceOf(SectionHasSameUpAndDownException.class);
    }

    @DisplayName("인접 구간인지 판단")
    @Test
    void isAdjacent() {
        Section 강남_잠실 = new Section(강남역, 잠실역, 10);
        Section 잠실_수서 = new Section(잠실역, 수서역, 10);

        assertTrue(잠실_수서.isAdjacent(강남_잠실));
    }

    @DisplayName("구간 거리")
    @Test
    void getDistance() {
        Section 강남_잠실 = new Section(강남역, 잠실역, 10);

        assertThat(강남_잠실.getDistance()).isEqualTo(10);
    }

    @DisplayName("같은역, 의미상 같은역 판단")
    @Test
    void isSameOrReversed() {
        Section section1 = new Section(강남역, 잠실역, 20);
        Section section1_1 = new Section(강남역, 잠실역, 20);
        Section section_reversed = new Section(잠실역, 강남역, 20);

        assertTrue(section1.isSameOrReversed(section1_1));
        assertTrue(section1.isSameOrReversed(section_reversed));
    }

}