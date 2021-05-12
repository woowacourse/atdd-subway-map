package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.InternalLogicConflictException;
import wooteco.subway.exception.section.SectionDuplicatedException;
import wooteco.subway.exception.section.SectionHasSameUpAndDownException;
import wooteco.subway.exception.section.SectionUnlinkedException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[도메인] Sections")
class SectionsTest {

    private static final Station 강남역 = Station.create(1L, "강남역");
    private static final Station 수서역 = Station.create(2L, "수서역");
    private static final Station 잠실역 = Station.create(3L, "잠실역");
    private static final Station 동탄역 = Station.create(4L, "동탄역");
    private static final Station 양재역 = Station.create(5L, "양재역");
    private static final Section 강남_수서 = Section.create(강남역, 수서역, 10);
    private static final Section 수서_강남 = Section.create(수서역, 강남역, 4);
    private static final Section 수서_잠실 = Section.create(수서역, 잠실역, 10);
    private static final Section 잠실_동탄 = Section.create(잠실역, 동탄역, 10);

    @DisplayName("구간 순서대로 역 보여주기")
    @Test
    void convertToSortedStations() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서, 잠실_동탄);
        Sections sections = Sections.create(setting);

        List<Station> stations = sections.convertToSortedStations();

        assertThat(stations).hasSize(4);
        assertThat(stations).containsExactly(강남역, 수서역, 잠실역, 동탄역);
    }

    @DisplayName("구간추가 - 성공")
    @Test
    void addAndThenGetModifiedAdjacen() {
        Sections sections = Sections.create(강남_수서);

        Section modifiedSection = sections.addAndThenGetModifiedAdjacent(수서_잠실);

        assertThat(sections.sections()).hasSize(2);
        assertThat(modifiedSection).isEqualTo(강남_수서);
    }

    @DisplayName("구간추가 - 실패(의미상 같은 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(() -> sections.addAndThenGetModifiedAdjacent(수서_강남))
                .isInstanceOf(SectionDuplicatedException.class);
        assertThatThrownBy(() -> sections.addAndThenGetModifiedAdjacent(강남_수서))
                .isInstanceOf(SectionDuplicatedException.class);
    }

    @DisplayName("구간추가 - 실패(앞뒤역이 같은 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_앞뒤같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(() -> sections.addAndThenGetModifiedAdjacent(Section.create(강남역, 강남역, 10)))
                .isInstanceOf(SectionHasSameUpAndDownException.class);
    }

    @DisplayName("구간추가 - 실패(연결불가 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_연결불가() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서);
        Section section = Section.create(동탄역, 양재역, 10);
        Sections sections = Sections.create(setting);

        assertThatThrownBy(() -> sections.addAndThenGetModifiedAdjacent(section))
                .isInstanceOf(SectionUnlinkedException.class);
    }

    @DisplayName("구간 가져오기")
    @Test
    void sections() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서, 잠실_동탄);
        Sections sections = Sections.create(setting);

        List<Section> result = sections.sections();

        assertThat(result).hasSize(3);
    }

    @DisplayName("삭제 -실패(내부로직 이상 테스트)")
    @Test
    void removeStationInBetween_내부구현로직이상() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서, 잠실_동탄);
        Sections sections = Sections.create(setting);

        assertThatThrownBy(() -> sections.removeStationInBetween(강남역))
                .isInstanceOf(InternalLogicConflictException.class);

    }

    @DisplayName("삭제")
    @Test
    void removeStationInBetween() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서);
        Sections sections = Sections.create(setting);

        Section result = sections.removeStationInBetween(수서역);

        assertThat(result).isEqualTo((Section.create(강남역, 잠실역, 20)));
    }

    @DisplayName("크기확인")
    @Test
    void hasSize() {
        List<Section> setting = Arrays.asList(강남_수서, 수서_잠실);

        Sections sections = Sections.create(setting);

        assertTrue(sections.hasSize(2));
    }
}