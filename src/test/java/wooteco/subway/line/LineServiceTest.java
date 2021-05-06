package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LineServiceTest {
    @Autowired
    private LineService lineService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineRequest lineRequest;

    @BeforeEach
    void setUp() {
        lineRequest = new LineRequest("2호선", "초록색", null, null, 0);
        jdbcTemplate.execute("delete from LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
    }

    @Test
    @DisplayName("노선 정상 생성 테스트")
    void createStation() {
        Line savedLine = lineService.createLine(lineRequest);
        assertEquals("2호선", savedLine.getName());
    }

    @Test
    @DisplayName("노선 이름 중복 생성 테스트")
    void createDuplicatedStation() {
        lineService.createLine(lineRequest);
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
                .isInstanceOf(LineExistenceException.class);
    }

    @Test
    @DisplayName("존재하지 않는 노선 ID 검색")
    void findNoneExistLineById() {
        String sql = "insert into Line (name, color) values (?, ?)";
        jdbcTemplate.update(sql, "2호선", "초록색");
        assertThatThrownBy(() -> lineService.findById(2L))
                .isInstanceOf(LineExistenceException.class);
    }
}