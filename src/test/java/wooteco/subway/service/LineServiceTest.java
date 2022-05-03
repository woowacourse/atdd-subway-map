package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;

class LineServiceTest {

    private LineService lineService = new LineService();

    @BeforeEach
    void setUp() {
        LineDao.clear();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void save() {
        Line line = new Line("신분당선","red");
        lineService.save(line);

        assertThatThrownBy(()->lineService.save(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 이름의 노선이 있습니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findAll() {
        Line line = new Line("신분당선","red");
        Line line2 = new Line("분당선","green");
        lineService.save(line);
        lineService.save(line2);

        assertThat(lineService.findAll())
                .containsOnly(line,line2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        Line line = new Line("신분당선","red");
        Line line2 = new Line("분당선","green");

        lineService.save(line);
        lineService.update(1L,line2);

        List<Line> lines = lineService.findAll();

        assertThat(lines.get(0).getName()).isEqualTo("분당선");
    }
}
