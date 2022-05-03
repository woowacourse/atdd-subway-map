package wooteco.subway.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.assembler.Assembler;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineDaoTest {

    private LineDao lineDao = Assembler.getLineDao();

    @AfterEach
    void afterEach() {
        lineDao.clear();
    }

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

    @Test
    @DisplayName("id에 맞는 노선을 조회한다.")
    void findById() {
        Line expected = lineDao.save(new Line("신분당선", "red"));

        Line actual = lineDao.findById(expected.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("id에 맞는 노선이 없을 경우 예외를 발생시킨다.")
    void findByIdException() {
        assertThatThrownBy(() -> lineDao.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageMatching("id에 맞는 지하철 노선이 없습니다.");
    }
}
