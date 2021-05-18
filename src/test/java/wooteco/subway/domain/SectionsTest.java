package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.SectionDuplicatedException;
import wooteco.subway.exception.section.SectionHasSameUpAndDownException;
import wooteco.subway.exception.section.SectionUnlinkedException;
import wooteco.subway.exception.station.StationNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("[도메인] Sections")
class SectionsTest {
    private Station 강남역;
    private Station 수서역;
    private Station 잠실역;
    private Station 동탄역;
    private Station 양재역;
    private Section 강남_수서;
    private Section 양재_수서;
    private Section 수서_양재;
    private Section 수서_강남;
    private Section 수서_잠실;
    private Section 잠실_동탄;

    @BeforeEach
    void setUp() {
        강남역 = Station.create(1L, "강남역");
        수서역 = Station.create(2L, "수서역");
        잠실역 = Station.create(3L, "잠실역");
        동탄역 = Station.create(4L, "동탄역");
        양재역 = Station.create(5L, "양재역");
        강남_수서 = Section.create(강남역, 수서역, 10);
        양재_수서 = Section.create(양재역, 수서역, 5);
        수서_양재 = Section.create(수서역, 양재역, 5);
        수서_강남 = Section.create(수서역, 강남역, 4);
        수서_잠실 = Section.create(수서역, 잠실역, 10);
        잠실_동탄 = Section.create(잠실역, 동탄역, 10);
    }



    @DisplayName("구간추가 - 성공(기존의 구간이 변경되는 경우)")
    @Test
    void addAndThenGetModifiedAdjacent() {
        Sections sections = Sections.create(강남_수서);

        Section modified = sections.modifyRelatedSectionToAdd(양재_수서);
        sections.add(modified);

        assertThat(sections.sections()).hasSize(2);
        assertThat(modified).isEqualTo(Section.create(강남역, 양재역, 5));
    }

    @DisplayName("구간추가 - 성공(기존의 구간이 변경되지 않는 경우)")
    @Test
    void addAndThenGetModifiedAdjacent_1() {
        Sections sections = Sections.create(강남_수서);

        Section modified = sections.modifyRelatedSectionToAdd(수서_잠실);
        sections.add(수서_잠실);

        assertThat(sections.sections()).hasSize(2);
        assertThat(modified).isEqualTo(강남_수서);
    }

    @DisplayName("구간추가 - 성공(가운데 추가 경우, 베이스가 꼬리)")
    @Test
    void addAndThenGetModifiedAdjacent_꼬리베이스() {
        Sections sections = Sections.create(수서_잠실);

        sections.modifyRelatedSectionToAdd(강남_수서);
        sections.add(강남_수서);

        Section modified = sections.modifyRelatedSectionToAdd(양재_수서);
        sections.add(양재_수서);

        assertThat(sections.sections()).hasSize(3);
        assertThat(modified).isEqualTo(Section.create(강남역, 양재역, 5));
    }

    @DisplayName("구간추가 - 성공(가운데 추가 경우, 베이스가 머리)")
    @Test
    void addAndThenGetModifiedAdjacent_머리베이스() {
        Sections sections = Sections.create(수서_잠실);
        sections.modifyRelatedSectionToAdd(강남_수서);
        sections.add(강남_수서);
        Section modified = sections.modifyRelatedSectionToAdd(수서_양재);
        sections.add(수서_양재);

        assertThat(sections.sections()).hasSize(3);
        assertThat(modified).isEqualTo(Section.create(양재역, 잠실역, 5));
    }

    @DisplayName("구간추가 - 실패(의미상 같은 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(() -> sections.modifyRelatedSectionToAdd(수서_강남))
                .isInstanceOf(SectionDuplicatedException.class);
        assertThatThrownBy(() -> sections.modifyRelatedSectionToAdd(강남_수서))
                .isInstanceOf(SectionDuplicatedException.class);
    }

    @DisplayName("구간추가 - 실패(앞뒤역이 같은 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_앞뒤같은구간() {
        Sections sections = Sections.create(강남_수서);

        assertThatThrownBy(() -> sections.modifyRelatedSectionToAdd(Section.create(강남역, 강남역, 10)))
                .isInstanceOf(SectionHasSameUpAndDownException.class);
    }

    @DisplayName("구간추가 - 실패(연결불가 구간 추가)")
    @Test
    void addAndThenGetModifiedAdjacent_실패_연결불가() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서);
        Section section = Section.create(동탄역, 양재역, 10);
        Sections sections = Sections.create(setting);

        assertThatThrownBy(() -> sections.modifyRelatedSectionToAdd(section))
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

    @DisplayName("삭제 -실패(포함되지 않는 역 삭제)")
    @Test
    void removeStationInBetween_내부구현로직이상() {
        List<Section> setting = Arrays.asList(수서_잠실);
        Sections sections = Sections.create(setting);

        assertThatThrownBy(() -> sections.removeRelatedSections(강남역))
                .isInstanceOf(StationNotFoundException.class);
    }

    @DisplayName("삭제")
    @Test
    void removeStationInBetween() {
        List<Section> setting = Arrays.asList(수서_잠실, 강남_수서);
        Sections sections = Sections.create(setting);

        List<Section> removed = sections.removeRelatedSections(수서역);
        Section result = sections.modifyRelatedSectionsToRemove(removed, 수서역);

        assertThat(result).isEqualTo((Section.create(강남역, 잠실역, 20)));
    }

    @DisplayName("크기확인")
    @Test
    void hasSize() {
        List<Section> setting = Arrays.asList(강남_수서, 수서_잠실);

        Sections sections = Sections.create(setting);

        assertTrue(sections.isSizeOf(2));
    }
}