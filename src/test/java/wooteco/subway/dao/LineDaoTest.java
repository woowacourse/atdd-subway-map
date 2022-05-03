package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.NoSuchLineException;

public class LineDaoTest {

    @BeforeEach
    void setUp() {
        LineDao.deleteAll();
    }

    @DisplayName("새로운 노선을 저장한다")
    @Test
    void saveLine() {
        // given
        Line line = new Line(1L, "line", "color");

        // when
        LineDao.save(line);

        // then
        List<Line> lines = LineDao.findAll();
        assertThat(lines.get(0)).isEqualTo(line);
    }

    @DisplayName("같은 이름의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateName() {
        // given
        LineDao.save(new Line(1L, "line", "color"));

        // when & then
        assertThatThrownBy(() -> LineDao.save(new Line(2L, "line", "color2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 이름");
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findAll() {
        // given
        LineDao.save(new Line(1L, "line1", "color1"));
        LineDao.save(new Line(2L, "line2", "color2"));
        LineDao.save(new Line(3L, "line3", "color3"));

        // when
        List<Line> lines = LineDao.findAll();

        // then
        assertThat(lines).hasSize(3);
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        // given
        LineDao.save(new Line(1L, "line1", "color1"));
        Line line = new Line(2L, "line2", "color2");
        Long savedId = LineDao.save(line);

        // when
        Line findLine = LineDao.findById(savedId);

        // then
        assertThat(findLine).isEqualTo(line);
    }

    @DisplayName("존재하지 않는 id로 노선을 조회하면 예외가 발생한다")
    @Test
    void throwExceptionWhenTargetLineDoesNotExist() {
        assertThatThrownBy(() -> LineDao.findById(1L))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("모든 노선을 제거한다")
    @Test
    void deleteAll() {
        // given
        LineDao.save(new Line(1L, "line", "color"));

        // when
        LineDao.deleteAll();

        // then
        List<Line> lines = LineDao.findAll();
        assertThat(lines).isEmpty();
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void update() {
        Long savedId = LineDao.save(new Line(1L, "line", "color"));

        Long updateId = LineDao.update(savedId, "changedName", "changedColor");

        Line findLine = LineDao.findById(updateId);
        assertThat(findLine.getName()).isEqualTo("changedName");
        assertThat(findLine.getColor()).isEqualTo("changedColor");
    }

    @DisplayName("없는 노선 정보를 변경하려 할 때, 예외를 던진다")
    @Test
    void throwExceptionWhenTryToUpdateNoLine() {
        assertThatThrownBy(() -> LineDao.update(1L, "changedName", "changedColor"))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        Long savedId = LineDao.save(new Line(1L, "line", "color"));

        // when
        LineDao.deleteById(savedId);

        // then
        assertThat(LineDao.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 노선을 제거하면 예외가 발생한다")
    @Test
    void throwExceptionWhenDeleteById() {
        assertThatThrownBy(() -> LineDao.deleteById(1L))
                .isInstanceOf(NoSuchLineException.class);
    }
}
