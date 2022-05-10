package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        final Station firstStation = new Station(1L, "1번역");
        final Station secondStation = new Station(2L, "2번역");
        final Section section = new Section(1L, firstStation, secondStation, 10);
        sections = new Sections(List.of(section));
    }

    @DisplayName("추가할 구간과 연결 가능한 역이 없으면 예외를 발생한다.")
    @Test
    void addSection_not_connect_exception() {
        final Station firstStation = new Station(3L, "3번역");
        final Station secondStation = new Station(4L, "4번역");
        final Section section = new Section(1L, firstStation, secondStation, 10);

        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연결 할 수 있는 상행역 또는 하행역이 없습니다.");
    }
}
