package wooteco.subway.line.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.NoSuchLineException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql("classpath:tableInit.sql")
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        String query = "INSERT INTO line(color, name) VALUES(?, ?)";
        jdbcTemplate.update(query, "bg-red-600", "신분당선");
        jdbcTemplate.update(query, "bg-green-600", "2호선");
    }

    @DisplayName("이름이랑 색깔을 입력받으면, DB에 line을 생성하고, id를 담은 line을 반환한다.")
    @Test
    void save() {
        Line line = new Line("bg-blue-600", "1호선");
        assertThat(lineDao.save(line).getId()).isEqualTo(3L);
    }

    @DisplayName("전체 line을 조회하면, DB에 존재하는 line 리스트를 반환한다.")
    @Test
    void getLines() {
        List<Line> expectedLines = Arrays.asList(
                new Line(1L, "bg-red-600", "신분당선"),
                new Line(2L, "bg-green-600", "2호선")
        );

        List<Line> lines = lineDao.findAll();
        assertThat(lines).usingRecursiveComparison().isEqualTo(expectedLines);
    }

    @DisplayName("전체 line을 조회할 때, DB에 존재하는 line이 없다면 빈 리스트를 반환한다.")
    @Test
    void getLines_noLinesSaved_emptyList() {
        jdbcTemplate.update("DELETE FROM line");

        List<Line> lines = lineDao.findAll();
        assertThat(lines).isEmpty();
    }

    @DisplayName("id를 통해 line을 조회하면, 해당 id에 매칭되는 line을 반환한다.")
    @Test
    void getLine() {
        Line expectedLine = new Line(1L, "bg-red-600", "신분당선");
        assertThat(lineDao.findById(1L).orElseThrow(NoSuchLineException::new)).isEqualTo(expectedLine);
    }

    @DisplayName("id를 통해 line 수정 요청을 보내면, DB에있는 line정보를 수정한다")
    @Test
    void update() {
        Line bunDangLine = new Line(1L, "bg-white-600", "분당선");
        lineDao.update(bunDangLine);

        String query = "SELECT id, color, name FROM line WHERE id = ?";
        Line line = jdbcTemplate.queryForObject(
                query,
                (resultSet, rowNum) -> new Line(
                        resultSet.getLong("id"),
                        resultSet.getString("color"),
                        resultSet.getString("name")
                ), 1L);

        assertThat(bunDangLine).isEqualTo(line);
    }

    @DisplayName("id를 통해 line을 삭제하면, DB에 있는 line을 삭제한다.")
    @Test
    void deleteById() {
        Long id = 1L;

        String query = "SELECT EXISTS(SELECT * FROM line WHERE id = ?)";
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isTrue();

        lineDao.deleteById(id);
        assertThat(jdbcTemplate.queryForObject(query, Boolean.class, id)).isFalse();
    }
}