package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.getLine;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Line line = new Line("신분당선", "red", 1L, 2L, 10);
    private final Line line2 = new Line("분당선", "green", 3L, 4L, 10);

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선을 저장하고 아이디로 찾는다.")
    @Test
    void saveAndFind() {
        //given
        Long id = lineDao.save(line);

        //when then
        assertThat(lineDao.findById(id))
                .isEqualTo(getLine(id, line));
    }

    @DisplayName("해당 이름의 지하철 노선이 있는지 여부를 확인한다.")
    @Test
    void hasLine_name() {
        lineDao.save(line);
        assertThat(lineDao.hasLine("신분당선"))
                .isTrue();
        assertThat(lineDao.hasLine("분당선"))
                .isFalse();
    }

    @DisplayName("해당 id의 지하철 노선이 있는지 여부를 확인한다.")
    @Test
    void hasLine_id() {
        Long id = lineDao.save(line);
        assertThat(lineDao.hasLine(id))
                .isTrue();
        assertThat(lineDao.hasLine(100L))
                .isFalse();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Long id1 = lineDao.save(line);
        Long id2 = lineDao.save(line2);

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines)
                .containsOnly(getLine(id1, line), getLine(id2, line2));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Long id = lineDao.save(line);

        //when
        lineDao.update(id, line2);

        //then
        assertThat(lineDao.hasLine("신분당선"))
                .isFalse();
        assertThat(lineDao.hasLine("분당선"))
                .isTrue();
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        //given
        Long id = lineDao.save(line);

        //when
        lineDao.delete(id);

        //then
        assertThat(lineDao.hasLine(id))
                .isFalse();
    }
}
