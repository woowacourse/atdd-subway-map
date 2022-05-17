package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private LineDao lineDao;

    private final Line line = new Line("신분당선", "red");

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate, dataSource);
    }

    @DisplayName("노선을 등록한다.")
    @Test
    void insert() {
        Line actual = lineDao.insert(line);

        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(line.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("id에 맞는 노선을 조회한다.")
    @Test
    void findById() {
        Line expected = lineDao.insert(line);

        Line actual = lineDao.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선 조회중 id에 맞는 노선이 없을 경우 예외를 발생시킨다.")
    @Test
    void findByIdException() {
        assertThatThrownBy(() -> lineDao.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("모든 노선 목록을 조회한다.")
    @Test
    void findAll() {
        Line savedLine1 = lineDao.insert(line);
        Line savedLine2 = lineDao.insert(new Line("1호선", "blue"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines).containsExactly(savedLine1, savedLine2);
    }

    @DisplayName("노선의 이름과 색깔을 수정한다.")
    @Test
    void update() {
        Line saveLine = lineDao.insert(line);
        Line expected = new Line(saveLine.getId(), "다른 분당선", "green");

        lineDao.update(expected);
        Line actual = lineDao.findById(saveLine.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Line saveLine = lineDao.insert(line);

        lineDao.delete(saveLine.getId());
        List<Line> lines = lineDao.findAll();

        assertThat(lines.contains(saveLine)).isFalse();
    }

    @DisplayName("id를 통해 노선의 존재 여부를 판단한다.")
    @Test
    void existLineById() {
        Line savedLine = lineDao.insert(line);
        boolean isExisted = lineDao.existLineById(savedLine.getId());

        assertThat(isExisted).isTrue();
    }
}
