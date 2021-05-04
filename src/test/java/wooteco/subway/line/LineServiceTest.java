package wooteco.subway.line;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LineServiceTest {
    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선 정상 생성 테스트")
    void createStation() {
        Line savedLine = lineService.createLine("2호선", "초록색");
        assertEquals("2호선", savedLine.getName());
    }

    @Test
    @DisplayName("노선 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        lineService.createLine("2호선", "초록색");
        assertThatThrownBy(() -> lineService.createLine("2호선", "초록색"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}