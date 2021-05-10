package wooteco.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.exception.LineExistenceException;
import wooteco.subway.line.exception.LineNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LineServiceTest {
    @Autowired
    private LineService lineService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineRequest lineRequest = new LineRequest("2호선", "초록색", null, null, 0);

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("truncate table LINE");
    }

    @Test
    @DisplayName("노선 정상 생성 테스트")
    void createStation() {
        Line savedLine = lineService.createLine(lineRequest);
        assertThat("2호선").isEqualTo(savedLine.getName());
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
                .isInstanceOf(LineNotFoundException.class);
    }

    @Test
    @DisplayName("노선 삭제 테스트")
    public void deleteLine() {
        Line savedLine = lineService.createLine(lineRequest);
        assertThat(lineService.findAll()).hasSize(1);

        lineService.deleteLine(savedLine.getId());
        assertThat(lineService.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("존재하지 않은 역 삭제 테스트")
    public void deleteNotExistingStation() {
        assertThatThrownBy(() -> lineService.deleteLine(1L))
                .isInstanceOf(LineNotFoundException.class);
    }
}