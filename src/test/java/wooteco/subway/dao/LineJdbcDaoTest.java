package wooteco.subway.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class LineJdbcDaoTest {

    private LineJdbcDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineJdbcDao(jdbcTemplate);
        List<Object[]> splitLine = new ArrayList<>(Arrays.asList(new String[]{"신분당선", "green"},
                new String[]{"3호선", "black"}, new String[]{"1호선", "red"}));
        jdbcTemplate.batchUpdate("INSERT INTO line (name, color) VALUES (?, ?)", splitLine);
    }

    @DisplayName("노선정보 저장")
    @Test
    void save() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600",
                1L, 2L, 10);
        Line line = lineDao.save(lineRequest);

        assertThat(line.getName()).isEqualTo("분당선");
    }

    @DisplayName("노선정보들을 가져온다.")
    @Test
    void findAll() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(3);
    }

    @DisplayName("노선 정보를 삭제한다.")
    @Test
    void delete() {
        LineRequest line = new LineRequest("4호선", "blue",
                1L, 2L, 10);
        Line lineResponse = lineDao.save(line);

        assertThat(lineDao.delete(lineResponse.getId())).isOne();
    }

    @DisplayName("노선 정보를 조회한다.")
    @Test
    void find() {
        LineRequest line = new LineRequest("5호선", "blue",
                1L, 2L, 10);
        Line lineResponse = lineDao.save(line);

        assertThat(lineDao.find(lineResponse.getId()).getName()).isEqualTo("5호선");
    }

    @DisplayName("노선 정보를 변경한다.")
    @Test
    void update() {
        LineRequest line = new LineRequest("7호선", "blue",
                1L, 2L, 10);
        Line lineResponse = lineDao.save(line);

        assertThat(lineDao.update(lineResponse.getId(), line)).isOne();
    }
}
