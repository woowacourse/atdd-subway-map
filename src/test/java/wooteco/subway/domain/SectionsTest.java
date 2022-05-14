package wooteco.subway.domain;

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

    @DisplayName("구간들에서 역 아이디들을 얻어옴")
    @Test
    void getAllStationId() {
        assertThat(sections.getAllStationId()).containsOnly(1L, 2L, 3L);
    }

    @DisplayName("방향이 일치하는 구간을 얻어옴")
    @Test
    void searchMatchedSection() {
        Section section = new Section(1L, 2L, 4L, 4);

        assertThat(sections.searchMatchedSection(section).getUpStationId()).isEqualTo(2L);
    }

    @DisplayName("구간을 추가할 수 있는지 확인")
    @Test
    void validateAddable() {
        Section section = new Section(1L, 2L, 3L, 4);

        assertThatThrownBy(() -> sections.validateAddable(section))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("삭제할 수 있는 역인지 확인")
    @Test
    void validateDeletable() {
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
