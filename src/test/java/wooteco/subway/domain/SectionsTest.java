package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private static Section section1to2;
    private static Section section2to3;
    private static Section section1to3;
    private static Section section5to4;
    private static Section section2to6;


    @BeforeAll
    static void setUp() {
        section1to2 = Section.of(1L, 2L, 10);
        section2to3 = Section.of(2L, 3L, 10);
        section1to3 = Section.of(1L, 3L, 20);
        section2to6 = Section.of(2L, 6L, 10);
        section5to4 = Section.of(5L, 4L, 10);
    }

    @Test
    @DisplayName("상행과 하행이 이미 등록된 경우 에러를 발생시킨다")
    void checkSectionErrorByAlreadyExist() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //then
        assertThatThrownBy(() -> sections.checkSection(section1to2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    @Test
    @DisplayName("상행과 하행이 존재하지 않는 경우 에러를 발생시킨다")
    void checkSectionErrorByNotExist() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //then
        assertThatThrownBy(() -> sections.checkSection(section5to4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
    }

    @Test
    @DisplayName("해당하는 Section과 겹치는 Section이 없는 경우 null을 return 시킨다.")
    void getTargetSectionBySectionNull() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //when
        Optional<Section> targetSectionBySection = sections.getTargetSectionToInsert(section5to4);

        //then
        assertThat(targetSectionBySection).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("해당하는 Section과 겹치는 Section이 있는 경우 해당 Section을 return한다.")
    void getTargetSectionBySection() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //when
        Optional<Section> targetSectionBySection = sections.getTargetSectionToInsert(section2to6);

        //then
        assertThat(targetSectionBySection).isEqualTo(Optional.of(section2to3));
    }

    @Test
    @DisplayName("상하행의 순서에 맞게 id list로 반환한다.")
    void convertToStationIds() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //when
        List<Long> stationIds = sections.convertToStationIds();

        //then
        assertThat(stationIds).containsExactly(1L, 2L, 3L);
    }

    @Test
    @DisplayName("sections의 길이가 1인 경우 에러를 발생시킨다.")
    void checkCanDeleteErrorBySizeOne() {
        //given
        Sections sections = new Sections(List.of(section1to2));

        //then
        assertThatThrownBy(sections::checkCanDelete)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 노선은 더 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 TargetSection을 찾을 때, 대상 section의 개수가 2개가 아니라면 에러가 발생한다.")
    void getMergedTargetSectionToDeleteErrorByWrongSectionSize() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //then
        assertThatThrownBy(()-> sections.getMergedTargetSectionToDelete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대상 Sections의 크기가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("삭제하려는 stationId 값을 통해서 노선에서 해당 id값을 가진 두 노선의 병합된 값을 가질 수 있다.")
    void getMergedTargetSectionToDelete() {
        //given
        Sections sections = new Sections(List.of(section1to2, section2to3));

        //when
        Section section = sections.getMergedTargetSectionToDelete(2L);

        //then
        assertAll(
                () -> assertThat(section.getDistance()).isEqualTo(section1to3.getDistance()),
                () -> assertThat(section.isSameUpStationId(section1to3)).isTrue(),
                () -> assertThat(section.isSameDownStationId(section1to3)).isTrue()
        );
    }
}