package wooteco.subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    private Line line;
    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section(new Station(1L, "잠실역"), new Station(2L, "삼전역"), 10);
        line = new Line("9호선", "bg-red-600", Arrays.asList(section));
    }


    @Test
    @DisplayName("라인 정상 생성 테스트 ")
    void create() {
        assertThatCode(() -> new Line("신분당선", "bg-red-600"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("초기 구간을 설정한다.")
    void addSection() {
        Line line = new Line("9호선", "bg-red-600");
        Section section = new Section(new Station("잠실역"), new Station("삼전역"), 10);

        line.initSections(Arrays.asList(section));
        // 나중에 size 비교해보기
    }

    @Test
    @DisplayName("구간 추가시 이미 등록된 구간이면 예외가 발생한다.")
    void addSectionException() {
        assertThatThrownBy(() -> line.addSection(section))
                .isInstanceOf(IllegalStateException.class);
    }
}
