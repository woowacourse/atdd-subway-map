package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
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
        Line line = new Line("신분당선", "red");

        //when
        assertThat(lineDao.findById(lineDao.save(line)).getName())
                .isEqualTo("신분당선");//then
    }

    @DisplayName("지하철 노선을 이름으로 검색한다.")
    @Test
    void findByName() {
        //given
        Line line = new Line("신분당선", "red");
        lineDao.save(line);

        //when
        assertThat(lineDao.findByName("신분당선").getName())
                .isEqualTo("신분당선");//then
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Line line = new Line("신분당선", "red");
        Line line1 = new Line("분당선", "green");

        //when
        lineDao.save(line);
        lineDao.save(line1);

        //then
        assertThat(lineDao.findAll())
                .hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");
        Long id = lineDao.save(line);

        //when
        lineDao.update(id, line2);

        //then
        assertThatThrownBy(() -> lineDao.findByName("신분당선"))
                .isInstanceOf(EmptyResultDataAccessException.class);
        assertThat(lineDao.findById(id).getName())
                .isEqualTo("분당선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        //given
        Line line = new Line("신분당선", "red");

        //when
        lineDao.delete(lineDao.save(line));

        //then
        assertThat(lineDao.findAll()).hasSize(0);
    }
}
