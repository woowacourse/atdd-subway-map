package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        Section section = new Section(1L, 1L, 2L, 4);
        Section section1 = new Section(1L, 2L, 3L, 4);
        sections = new Sections(List.of(section, section1));
    }

    @DisplayName("구간들에서 모든 역 아이디들을 얻어옴")
    @Test
    void getAllStationId() {
        assertThat(sections.getAllStationId()).containsOnly(1L, 2L, 3L);
    }

    @DisplayName("상행이 일치하는 구간을 얻어와서 확인")
    @Test
    void searchUpMatchedSection() {
        Section section = new Section(1L, 2L, 4L, 4);

        assertThat(sections.getMatchedSection(section).getUpStationId()).isEqualTo(2L);
        assertThat(sections.getMatchedSection(section).getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("하행이 일치하는 구간을 얻어와서 확인")
    @Test
    void searchDownMatchedSection() {
        Section section = new Section(1L, 4L, 3L, 4);

        assertThat(sections.getMatchedSection(section).getUpStationId()).isEqualTo(2L);
        assertThat(sections.getMatchedSection(section).getDownStationId()).isEqualTo(3L);
    }

    @DisplayName("일치하는 구간을 얻어 올 수 없음")
    @Test
    void canNotSearchMatchedSection() {
        Section section = new Section(1L, 3L, 4L, 4);

        assertThatThrownBy(() -> sections.getMatchedSection(section))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("일치하는 구간이 없습니다.");
    }

    @DisplayName("구간을 추가 등록할 수 있음을 검증")
    @Test
    void addableSection() {
        Section section = new Section(1L, 3L, 4L, 4);

        assertThatCode(() -> sections.validateAddable(section))
            .doesNotThrowAnyException();
    }

    @DisplayName("역이 모두 등록되어 있지 않으면 구간을 추가할 수 없음을 검증")
    @Test
    void canNotAddWhenNotAllExistStation() {
        Section section = new Section(1L, 4L, 5L, 4);

        assertThatThrownBy(() -> sections.validateAddable(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
    }

    @DisplayName("역이 모두 등록되어 있으면 구간을 추가할 수 없음을 검증")
    @Test
    void canNotAddWhenAllExistStation() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThatThrownBy(() -> sections.validateAddable(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("역을 삭제할 수 있음을 검증")
    @Test
    void deletableStation() {
        assertThatCode(() -> sections.validateDeletable(2L))
            .doesNotThrowAnyException();
    }

    @DisplayName("구간이 하나 남았을 때 역을 삭제할 수 없음을 검증")
    @Test
    void canNotDeleteWhenExistOneSection() {
        Section section = new Section(1L, 2L, 3L, 4);
        Sections newSections = new Sections(List.of(section));

        assertThatThrownBy(() -> newSections.validateDeletable(2L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("구간이 하나 남아서 삭제 할 수 없음");
    }

    @DisplayName("존재하지 않는 역을 삭제하려고 할 때 삭제할 수 없음을 검증")
    @Test
    void canNotDeleteWhenNotExistStation() {
        assertThatThrownBy(() -> sections.validateDeletable(5L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("역이 존재하지 않습니다.");
    }

    @DisplayName("하나의 역을 빼고 구간을 합침")
    @Test
    void createCombineSection() {
        Section resultSection = sections.createCombineSection(2L);

        assertThat(resultSection.getDistance()).isEqualTo(8);
        assertThat(resultSection.getUpStationId()).isEqualTo(1L);
        assertThat(resultSection.getDownStationId()).isEqualTo(3L);
    }
}
