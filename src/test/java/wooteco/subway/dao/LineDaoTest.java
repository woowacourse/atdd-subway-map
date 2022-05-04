package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;

@SpringBootTest
@Transactional
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("노선을 등록할 수 있다.")
    void save() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        final Long savedId = lineDao.save(line);

        // then
        final Line findLine = lineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-green-600");
        lineDao.save(line1);
        lineDao.save(line2);

        // when
        List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines).hasSize(2)
                .extracting("name", "color")
                .containsExactlyInAnyOrder(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600"));
    }

    @Test
    @DisplayName("단건 노선을 조회한다.")
    void findById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Long savedId = lineDao.save(line);

        // when
        final Line findLine = lineDao.findById(savedId);

        // then
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Long savedId = lineDao.save(line);

        // when
        final Line updateLine = new Line(savedId, "다른분당선", "bg-red-600");
        lineDao.updateById(updateLine);

        // then
        final Line findLine = lineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Long savedId = lineDao.save(line);

        // when & then
        assertDoesNotThrow(() -> lineDao.deleteById(savedId));
    }
}
