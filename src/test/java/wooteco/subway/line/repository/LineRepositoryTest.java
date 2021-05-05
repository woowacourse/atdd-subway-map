package wooteco.subway.line.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableInit.sql")
class LineRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepository(jdbcTemplate);
        String query = "INSERT INTO line(color, name) VALUES(?, ?)";
        jdbcTemplate.update(query, "bg-red-600", "신분당선");
        jdbcTemplate.update(query, "bg-green-600", "2호선");
    }

    @DisplayName("이름이랑 색깔을 입력받으면, DB에 Line을 생성하고, id를 반환한다.")
    @Test
    void save() {
        Line line = new Line("bg-blue-600", "1호선");
        assertThat(lineRepository.save(line)).isEqualTo(3L);
    }

    @DisplayName("DB에 존재하는 Line이면, true를 반환한다.")
    @Test
    void isExist() {
        Line line1 = new Line("bg-blue-600", "1호선");
        Line line2 = new Line("bg-green-600", "2호선");
        assertThat(lineRepository.isExist(line1)).isFalse();
        assertThat(lineRepository.isExist(line2)).isTrue();
    }
}