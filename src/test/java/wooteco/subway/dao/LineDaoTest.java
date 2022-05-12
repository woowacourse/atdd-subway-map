package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.Fixtures.LINE;
import static wooteco.subway.Fixtures.LINE_2;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

@JdbcTest
@Transactional
class LineDaoTest {
    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선을 저장하고 아이디로 찾는다.")
    @Test
    void saveAndFind() {
        //given
        Long id = lineDao.save(LINE);

        //when then
        assertThat(lineDao.findById(id))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(LINE);
    }

    @DisplayName("해당 이름의 지하철 노선이 있는지 여부를 확인한다.")
    @Test
    void hasLine_name() {
        lineDao.save(LINE);
        assertThat(lineDao.hasLine("신분당선"))
                .isTrue();
        assertThat(lineDao.hasLine("분당선"))
                .isFalse();
    }

    @DisplayName("해당 id의 지하철 노선이 있는지 여부를 확인한다.")
    @Test
    void hasLine_id() {
        Long id = lineDao.save(LINE);
        assertThat(lineDao.hasLine(id))
                .isTrue();
        assertThat(lineDao.hasLine(100L))
                .isFalse();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Long id1 = lineDao.save(LINE);
        Long id2 = lineDao.save(LINE_2);

        //when
        List<Line> lines = lineDao.findAll();

        //then
        assertThat(lines)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(LINE, LINE_2));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Long id = lineDao.save(LINE);

        //when
        lineDao.update(id, LINE_2);

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
        Long id = lineDao.save(LINE);

        //when
        lineDao.delete(id);

        //then
        assertThat(lineDao.hasLine(id))
                .isFalse();
    }
}
