package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("노선 저장")
    @Test
    void save() {
        // given
        Line line = new Line("분당선", "yellow");

        // when
        Long id = lineDao.save(line);

        // then
        assertThat(id).isEqualTo(3L);
    }

    @DisplayName("노선 이름으로 개수 검색")
    @Test
    void countByName() {
        // given
        String name = "신분당선";

        // when
        int count = lineDao.countByName(name);

        // then
        assertThat(count).isEqualTo(1);
    }

    @DisplayName("노선 이름으로 검색")
    @Test
    void findById() {
        // given
        Line expected = new Line(1L, "신분당선", "red");

        // when
        Line line = lineDao.findById(1L);

        // then
        assertThat(line).isEqualTo(expected);
    }

    @DisplayName("노선 전체 조회")
    @Test
    void findAll() {
        // given

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("노선 정보 수정")
    @Test
    void update() {
        // given
        Long id = 1L;
        LineRequest lineRequest = new LineRequest("신분당선", "pink");

        // when
        lineDao.update(id, lineRequest);

        // then
        Line line = lineDao.findById(id);
        assertThat(line.getColor()).isEqualTo(lineRequest.getColor());
    }

    @DisplayName("노선 삭제")
    @Test
    void deleteById() {
        // given
        Long id = 1L;

        // when
        lineDao.deleteById(id);

        // then
        assertThatThrownBy(() -> lineDao.findById(id)).isInstanceOf(EmptyResultDataAccessException.class);
    }
}