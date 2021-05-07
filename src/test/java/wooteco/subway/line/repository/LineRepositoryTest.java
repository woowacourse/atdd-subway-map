package wooteco.subway.line.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;

import java.util.Arrays;
import java.util.List;

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
        assertThat(lineRepository.save(line).getId()).isEqualTo(3L);
    }

    @DisplayName("DB에 존재하는 Line이면, true를 반환한다.")
    @Test
    void isExist() {
        Line line1 = new Line("bg-blue-600", "1호선");
        Line line2 = new Line("bg-green-600", "2호선");
        assertThat(lineRepository.isExistName(line1)).isFalse();
        assertThat(lineRepository.isExistName(line2)).isTrue();
    }

    @DisplayName("전체 Line을 조회하면, DB에 존재하는 Line 리스트를 반환한다.")
    @Test
    void findAll() {
        List<Line> expectedLines = Arrays.asList(
                new Line(1L, "bg-red-600", "신분당선"),
                new Line(2L, "bg-green-600", "2호선")
        );

        List<Line> lines = lineRepository.getLines();
        assertThat(lines).usingRecursiveComparison().isEqualTo(expectedLines);
    }

    @DisplayName("id를 통해 Line을 조회하면, 해당 id에 매칭되는 Line을 반환한다.")
    @Test
    void getLine() {
        Line expectedLine = new Line(1L, "bg-red-600", "신분당선");
        assertThat(lineRepository.getLineById(1L)).isEqualTo(expectedLine);
    }

    @DisplayName("id를 통해 Line 수정 요청을 보내면, DB에있는 Line정보를 수정한다")
    @Test
    void update() {
        Line bundangLine = new Line(1L, "bg-white-600", "분당선");
        lineRepository.update(bundangLine);

        String query = "SELECT color, name FROM line WHERE id = ?";
        Line line = jdbcTemplate.queryForObject(
                query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getString("color"),
                        resultSet.getString("name")
                ), 1L);

        assertThat(bundangLine).isEqualTo(line);
    }

    @DisplayName("id를 통해 Line을 삭제하면, DB에 있는 Line을 삭제한다.")
    @Test
    void deleteById(){
        Long id = 1L;

        String query = "SELECT EXISTS(SELECT * FROM line WHERE id = ?)";
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isTrue();

        lineRepository.deleteById(id);
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isFalse();
    }
}