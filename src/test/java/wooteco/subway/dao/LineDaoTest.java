package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @AfterEach
    void rollback() {
        LineDao.deleteAll();
    }

    @Test
    @DisplayName("노선을 등록할 수 있다.")
    void save() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");

        // when
        final Long savedId = LineDao.save(line);

        // then
        final Line findLine = LineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-green-600");
        LineDao.save(line1);
        LineDao.save(line2);

        // when
        List<Line> lines = LineDao.findAll();

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
        final Long savedId = LineDao.save(line);

        // when
        final Line findLine = LineDao.findById(savedId);

        // then
        assertThat(findLine).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        final Line line = new Line("신분당선", "bg-red-600");
        final Long savedId = LineDao.save(line);

        // when
        final Line updateLine = new Line(savedId, "다른분당선", "bg-red-600");
        LineDao.updateById(updateLine);

        // then
        final Line findLine = LineDao.findById(savedId);
        assertThat(findLine).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Nested
    @DisplayName("노선 id를 가지고")
    class DeleteById {
        @Test
        @DisplayName("삭제할 수 있다.")
        void valid() {
            // given
            final Line line = new Line("신분당선", "bg-red-600");
            final Long savedId = LineDao.save(line);

            // when & then
            assertDoesNotThrow(() -> LineDao.deleteById(savedId));
        }

        @Test
        @DisplayName("id가 없는 경우 예외가 발생한다.")
        void invalidOfNumber() {
            // given
            final Line line = new Line("신분당선", "bg-red-600");
            final Long savedId = LineDao.save(line);

            // when & then
            assertThatThrownBy(() -> LineDao.deleteById(savedId + 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
