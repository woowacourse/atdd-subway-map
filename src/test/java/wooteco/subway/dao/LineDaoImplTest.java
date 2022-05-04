package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class LineDaoImplTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDaoImpl(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE line IF EXISTS");
        jdbcTemplate.execute("create table if not exists LINE(\n" +
                "id bigint auto_increment not null,\n" +
                "name varchar(255) not null unique,\n" +
                "color varchar(20) not null,\n" +
                "primary key(id));");

        List<Object[]> splitLine = new ArrayList<>(Arrays.asList(new String[]{"신분당선", "green"},
                new String[]{"3호선", "black"}, new String[]{"1호선", "red"}));

        jdbcTemplate.batchUpdate("INSERT INTO line (name, color) VALUES (?, ?)", splitLine);
    }

    @DisplayName("노선정보를 저장한다.")
    @Test
    void save() {
        Line line = new Line("분당선", "green");
        Line newLine = lineDao.save(line);

        assertThat(newLine.getName()).isEqualTo("분당선");
    }

    @DisplayName("노선정보들을 가져온다.")
    @Test
    void findAll() {
        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(3);
    }

    @DisplayName("노선정보를 삭제한다.")
    @Test
    void delete() {
        Line line = new Line("4호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.delete(newLine.getId())).isOne();
    }

    @DisplayName("노선정보를 조회한다.")
    @Test
    void find() {
        Line line = new Line("5호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.find(newLine.getId()).getName()).isEqualTo("5호선");
    }

    @DisplayName("노선정보를 변경한다.")
    @Test
    void update() {
        Line line = new Line("7호선", "blue");
        Line newLine = lineDao.save(line);

        assertThat(lineDao.update(newLine.getId(), line)).isOne();
    }
}
