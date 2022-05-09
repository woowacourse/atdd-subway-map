package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
public class LineDaoTest {
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        lineDao = new LineDao(jdbcTemplate);

        jdbcTemplate.execute("DROP TABLE Line IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE Line(" +
                "id bigint auto_increment not null,\n" +
                "name varchar(255) not null unique,\n" +
                "color varchar(20) not null,\n" +
                "upStationId bigint not null,\n" +
                "downStationId bigint not null,\n" +
                "distance bigint not null,\n" +
                "primary key(id))"
        );
    }

    @DisplayName("Line 객체의 정보가 제대로 저장되는 것을 확인한다.")
    @Test
    void save() {
        final Section section = new Section(1L, 2L, 10);
        final Line line = lineDao.save(new Line("2호선", "#009D3E", section));

        assertThat(line.getName()).isEqualTo("2호선");
    }

    @DisplayName("전달된 이름을 가지고 있는 Line의 개수를 제대로 반환하는 지 확인한다.")
    @Test
    void counts_one() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "2호선", "##009D3E", 1L, 2L, 10);
        final int actual = lineDao.counts("2호선");

        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("전달된 이름을 가지고 있는 Line의 개수를 제대로 반환하는 지 확인한다.")
    @Test
    void counts_zero() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "2호선", "##009D3E", 1L, 2L, 10);
        final int actual = lineDao.counts("1호선");

        assertThat(actual).isEqualTo(0);
    }

    @DisplayName("모든 Line을 가져오는 것을 확인한다.")
    @Test
    void findAll() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "1호선", "#0052A4", 1L, 2L, 10);
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "2호선", "##009D3E", 1L, 3L, 10);
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "3호선", "#EF7C1C", 1L, 4L, 10);
        final List<Line> lines = lineDao.findAll();
        final int actual = lines.size();

        assertThat(actual).isEqualTo(3);
    }

    @DisplayName("인자로 전달된 id를 가지는 Line을 가져오는 것을 확인한다.")
    @Test
    void findById() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "1호선", "#0052A4", 1L, 2L, 10);
        final Line line = lineDao.findById(1L);
        final String actual = line.getName();

        assertThat(actual).isEqualTo("1호선");
    }

    @DisplayName("인자로 전달된 id를 가지는 레코드가 올바르게 수정되는 것을 확인한다.")
    @Test
    void edit() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "1호선", "#0052A4", 1L, 2L, 10);
        lineDao.edit(1L, "2호선", "#009D3E");
        final String actual = jdbcTemplate.queryForObject("SELECT name FROM Line WHERE id = 1", String.class);

        assertThat(actual).isEqualTo("2호선");
    }

    @DisplayName("인자로 전달된 id를 가지는 레코드가 삭제되는 것을 확인한다.")
    @Test
    void deleteById() {
        jdbcTemplate.update("insert into Line (name, color, upStationId, downStationId, distance) values (?, ?, ?, ?, ?)",
                "1호선", "#0052A4", 1L, 2L, 10);
        lineDao.deleteById(1L);
        final int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Line WHERE name = '1호선'", Integer.class);

        assertThat(actual).isEqualTo(0);
    }
}