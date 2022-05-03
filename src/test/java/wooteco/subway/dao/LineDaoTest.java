package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LineDaoTest {

    private LineDao lineDao = Assembler.getLineDao();

    @Test
    @DisplayName("노선을 등록한다.")
    void save() {
        Line expected = new Line("신분당선", "red");
        Line actual = lineDao.save(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("모든 노선 목록을 조회한다.")
    void findAll() {
        lineDao.save(new Line("신분당선", "red"));
        lineDao.save(new Line("1호선", "blue"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines.size()).isEqualTo(2);
    }
}
