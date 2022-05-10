package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private static final Section SECTION_LINE_1_A = new Section(1L, 1L, 2L, 10);
    private static final Section SECTION_LINE_1_B = new Section(1L, 1L, 3L, 12);

    @DisplayName("특정 노선에 속한 구간정보를 생성한다")
    @Test
    void create_success() {
        final List<Section> sections = List.of(SECTION_LINE_1_A, SECTION_LINE_1_B);

        assertDoesNotThrow(() -> new Sections(sections));
    }

    @DisplayName("특정 노선에 구간이 존재하지 않는다면 구간정보를 생성할 수 없다.")
    @Test
    void create_fail() {
        assertThatThrownBy(() -> new Sections(new ArrayList<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 존재하지 않는 구간입니다.");
    }
}
