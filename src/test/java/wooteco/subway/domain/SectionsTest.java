package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.DuplicatedSectionException;
import wooteco.subway.exception.section.SectionCycleException;
import wooteco.subway.exception.section.SectionHasSameUpAndDownException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[도메인] Sections")
class SectionsTest {

    private static final Station 강남역 = Station.create(1L, "강남역");
    private static final Station 수서역 = Station.create(2L, "수서역");
    private static final Station 잠실역 = Station.create(3L, "잠실역");
    private static final Section 강남_수서 = Section.create(강남역, 수서역, 10);
    private static final Section 수서_강남 = Section.create(수서역, 강남역, 4);
    private static final Section 수서_잠실 = Section.create(수서역, 잠실역, 10);

    @Test
    void convertToSortedStations() {
    }

    @DisplayName("구간추가 - 성공")
    @Test
    void add() {
        Sections sections = Sections.create(강남_수서);

        Section modifiedSection = sections.addAndThenGetModifiedAdjacent(수서_잠실);

        assertThat(sections.sections()).hasSize(2);
        assertThat(modifiedSection).isEqualTo(강남_수서);
    }

    @DisplayName("구간추가 - 살퍄(의미상 같은 구간 추가)")
    @Test
    void add_실패_같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(()->sections.addAndThenGetModifiedAdjacent(수서_강남))
                .isInstanceOf(DuplicatedSectionException.class);
        assertThatThrownBy(()->sections.addAndThenGetModifiedAdjacent(강남_수서))
                .isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("구간추가 - 살퍄(앞뒤역이 같은 구간 추가)")
    @Test
    void add_실패_앞뒤같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(()->sections.addAndThenGetModifiedAdjacent(Section.create(강남역, 강남역, 10)))
                .isInstanceOf(SectionHasSameUpAndDownException.class);
    }

    @Test
    void sections() {
    }

    @DisplayName("삭제시 내부로직 이상 테스트")
    @Test
    void mergeTwoIntoOne() {

    }

    @DisplayName("크기확인")
    @Test
    void hasSize() {
        List<Section> setting = Arrays.asList(강남_수서, 수서_잠실);

        Sections sections = Sections.create(setting);

        assertTrue(sections.hasSize(2));
    }
}