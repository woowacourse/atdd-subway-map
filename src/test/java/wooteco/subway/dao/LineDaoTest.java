package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dto.line.LineRequest;
import wooteco.subway.dto.line.LineResponse;

@JdbcTest
class LineDaoTest {

    private LineDao lineDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineResponse line;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
        line = lineDao.save(new LineRequest("1호선", "blue"));
    }

    @Test
    @DisplayName("노선을 추가한다")
    void save() {
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("1호선"),
                () -> assertThat(line.getColor()).isEqualTo("blue")
        );
    }

    @Test
    @DisplayName("특정 노선 조회")
    void findById() {
        assertThat(lineDao.findById(line.getId())).isEqualTo(line);
    }

    @Test
    void findAll() {
        //given
        var line2 = lineDao.save(new LineRequest("2호선", "green"));

        //when
        var lines = lineDao.findAll();

        //then
        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).contains(line),
                () -> assertThat(lines).contains(line2)
        );
    }

    @Test
    void updateById() {
        var id = line.getId();
        lineDao.update(id, "2호선", "black");

        assertThat(lineDao.findById(id)).isEqualTo(new LineResponse(id, "2호선", "black"));
    }

    @Test
    void deleteById() {
        lineDao.deleteById(line.getId());

        assertThat(lineDao.findAll()).hasSize(0);
    }
}