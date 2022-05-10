package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private static final Section SECTION_LINE_1_2_10 = new Section(1L, 1L, 2L, 10);
    private static final Section SECTION_LINE_1_3_12 = new Section(1L, 1L, 3L, 12);

    @DisplayName("특정 노선에 속한 구간정보를 생성한다")
    @Test
    void create_success() {
        final List<Section> sections = List.of(SECTION_LINE_1_2_10, SECTION_LINE_1_3_12);

        assertDoesNotThrow(() -> new Sections(sections));
    }

    @DisplayName("특정 노선에 구간이 존재하지 않는다면 구간정보를 생성할 수 없다.")
    @Test
    void create_fail() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 존재하지 않는 구간입니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 중 하나만 같은 구간은 구간 등록이 가능하다.")
    @Test
    void canAddSection_valid_only_one_station_same() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_2_10, SECTION_LINE_1_3_12));
        final Section targetSection = new Section(1L, 1L, 3L, 12);

        Assertions.assertThatThrownBy(() -> sections.canAddSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 구간을 추가하기 위해선 상행 종점 혹은 하행 종점 둘 중 하나만 포함한 구간만 가능합니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 모두 같은 구간은 추가시 예외가 발생한다.")
    @Test
    void canAddSection_invalid_all_same_up_and_down_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_2_10, SECTION_LINE_1_3_12));
        final Section targetSection = new Section(1L, 1L, 3L, 12);

        Assertions.assertThatThrownBy(() -> sections.canAddSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 구간을 추가하기 위해선 상행 종점 혹은 하행 종점 둘 중 하나만 포함한 구간만 가능합니다.");
    }

    @DisplayName("기존 구간정보와 상행, 하행 종점 모두 다른 구간은 추가시 예외가 발생한다.")
    @Test
    void canAddSection_invalid_all_not_same_up_and_down_station() {
        final Sections sections = new Sections(List.of(SECTION_LINE_1_2_10, SECTION_LINE_1_3_12));
        final Section targetSection = new Section(1L, 2L, 4L, 12);

        Assertions.assertThatThrownBy(() -> sections.canAddSection(targetSection))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("[ERROR] 구간을 추가하기 위해선 상행 종점 혹은 하행 종점 둘 중 하나만 포함한 구간만 가능합니다.");
    }
}
