package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

@JdbcTest
class LineServiceTest {
    private LineService lineService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        LineDao lineDao = new LineDao(jdbcTemplate);
        lineService = new LineService(lineDao);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        Line line = new Line("신분당선", "red");
        lineService.save(line);
    }

    @DisplayName("이미 있는 이름의 지하철 노선을 저장할 수 없다.")
    @Test
    void save_error() {
        //given
        Line line = new Line("신분당선", "red");

        //when
        lineService.save(line);

        //then
        assertThatThrownBy(() -> lineService.save(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        //given
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");
        lineService.save(line);
        lineService.save(line2);

        //when
        assertThat(lineService.findAll())
                .hasSize(2);//then
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        //given
        Line line = new Line("신분당선", "red");
        Line line2 = new Line("분당선", "green");

        //when
        lineService.update(lineService.save(line).getId(), line2);

        //then
        assertThat(lineService.findAll().get(0).getName())
                .isEqualTo("분당선");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void delete() {
        //given
        Line line = new Line("신분당선", "red");

        //when
        lineService.delete(lineService.save(line).getId());

        //then
        assertThat(lineService.findAll()).hasSize(0);
    }
}
