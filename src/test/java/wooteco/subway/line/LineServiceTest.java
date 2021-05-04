package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LineServiceTest {
    @Test
    @DisplayName("노선 정상 생성 테스트")
    void createStation() {
        LineService lineService = new LineService();
        Line savedLine = lineService.createLine("2호선", "초록색");
        assertEquals("2호선", savedLine.getName());
    }

    @Test
    @DisplayName("노선 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        LineService lineService = new LineService();
        lineService.createLine("2호선", "초록색");
        assertThatThrownBy(() -> lineService.createLine("2호선", "초록색"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}