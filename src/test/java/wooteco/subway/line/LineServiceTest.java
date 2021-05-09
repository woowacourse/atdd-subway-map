package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.exception.LineException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LineServiceTest {
    private static final LineRequest lineRequest = new LineRequest("2호선", "초록색",1L,2L,3);

    @Autowired
    private LineService lineService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("delete from LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
    }

    @Test
    @DisplayName("노선 정상 생성 테스트")
    void createStation() {
        LineResponse lineResponse = lineService.createLine(lineRequest);
        assertEquals("2호선", lineResponse.getName());
    }

    @Test
    @DisplayName("노선 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        lineService.createLine(lineRequest);
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
                .isInstanceOf(LineException.class);
    }
}