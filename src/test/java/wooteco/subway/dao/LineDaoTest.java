package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@JdbcTest
class LineDaoTest {

    private Long savedId;

    private LineDao lineDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        lineDao = new LineDao(jdbcTemplate);
        savedId = lineDao.save(new Line("2호선", "green"));
        lineDao.save(new Line("3호선", "orange"));
    }

    @Test
    @DisplayName("이름을 이용하여 line 을 저장한다.")
    void save() {
        //given
        String name = "4호선";
        String color = "blue";
        //when
        Long id = lineDao.save(new Line(name, color));
        //then
        Line line = lineDao.findById(id);
        assertThat(line.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("이름을 이용하여 line 이 존재하는지 확인한다.")
    void existByName() {
        //given
        String name = "2호선";
        //when
        Boolean exist = lineDao.existByName(name);
        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("색을 이용하여 line 이 존재하는지 확인한다.")
    void existByColor() {
        //given
        String color = "green";
        //when
        Boolean exist = lineDao.existByColor(color);
        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("id를 이용하여 line 을 조회한다.")
    void findById() {
        //given

        //when
        Line line = lineDao.findById(savedId);
        //then
        assertThat(line.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("모든 line 을 조회한다.")
    void findAll() {
        //given

        //when
        List<Line> lines = lineDao.findAll();
        //then
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("line 를 수정한다.")
    void update() {
        //given
        String name = "분당선";
        LineRequest lineRequest = new LineRequest(name, "white", 0L, 0L, 0);
        //when
        lineDao.update(savedId, lineRequest);
        //then
        Line line = lineDao.findById(savedId);
        assertThat(line.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("id 를 이용하여 line 을 삭제한다.")
    void deleteById() {
        //given

        //when
        lineDao.deleteById(savedId);
        //then
        List<Line> lines = lineDao.findAll();
        assertThat(lines.size()).isEqualTo(1);
    }

}