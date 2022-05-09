package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ExceptionMessage;

class SectionTest {

    @Test
    @DisplayName("구간을 다른 구간으로 쪼개기")
    void insert_divide() {
        // given
        Section section = new Section(1L, 2L, 4);
        Section sectionForDivide = new Section(1L, 3L, 1);

        // when
        List<Section> dividedSections = section.insert(sectionForDivide);

        // then
        assertThat(dividedSections).hasSize(2);
        assertThat(dividedSections)
                .containsOnly(new Section(3L, 2L, 3), sectionForDivide);
    }

    @Test
    @DisplayName("구간을 다른 구간으로 연장하기")
    void insert_add() {
        // given
        Section section = new Section(1L, 2L, 4);
        Section sectionForAdd = new Section(2L, 3L, 1);

        // when
        List<Section> addedSections = section.insert(sectionForAdd);

        // then
        assertThat(addedSections).hasSize(2);
        assertThat(addedSections)
                .containsOnly(section, sectionForAdd);
    }

    @Test
    @DisplayName("연결할 역이 없는 경우 예외 생성하기")
    void insert_invalidNotMatch() {
        // given
        Section section = new Section(1L, 2L, 4);
        Section sectionForAdd = new Section(3L, 4L, 1);

        // when
        assertThatThrownBy(() -> section.insert(sectionForAdd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.INSERT_SECTION_NOT_MATCH.getContent());
    }

    @Test
    @DisplayName("삽입되는 구간이 쪼개지는 구간 길이보다 긴 경우 예외 생성하기")
    void insert_invalidDistance() {
        // given
        Section section = new Section(1L, 2L, 4);
        Section sectionForAdd = new Section(1L, 3L, 4);

        // when
        assertThatThrownBy(() -> section.insert(sectionForAdd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.INVALID_INSERT_SECTION_DISTANCE.getContent());
    }

    @Test
    @DisplayName("같은 출발점과 도착점을 삽입하려는 경우 예외 생성하기")
    void insert_invalidSection() {
        // given
        Section section = new Section(1L, 2L, 4);
        Section sectionForAdd = new Section(1L, 2L, 5);

        // when
        assertThatThrownBy(() -> section.insert(sectionForAdd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessage.SAME_STATIONS_SECTION.getContent());
    }
}
