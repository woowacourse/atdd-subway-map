package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @AfterEach
    private void rollback() {
        LineDao.deleteAll();
    }

    @Test
    @DisplayName("노선 이름이 중복되면 예외가 발생한다.")
    void save_inValidName() {
        // given
        final Line line = new Line("7호선", "bg-red-600");
        LineDao.save(line);

        // when
        final Line newLine = new Line("7호선", "bg-green-600");

        // then
        assertThatThrownBy(() -> LineDao.save(newLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름의 노선은 저장할 수 없습니다.");
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void findAll() {
        // given
        final Line line7 = new Line("7호선", "bg-red-600");
        LineDao.save(line7);

        final String line5Name = "5호선";
        final String line5Color = "bg-green-600";
        final Line line5 = new Line(line5Name, line5Color);
        LineDao.save(line5);

        // when
        final List<Line> lines = LineDao.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines).contains(line7, new Line(line5Name, line5Color));
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 조회한다.")
    void findById() {
        // given
        final Line line7 = new Line("7호선", "bg-red-600");
        Line persistLine = LineDao.save(line7);

        // when
        Line actual = LineDao.findById(persistLine.getId());

        // then
        assertThat(actual).isEqualTo(persistLine);
    }

    @Test
    @DisplayName("존재하지 않은 id로 노선을 조회하면 예외가 발생한다.")
    void findById_invalidId() {
        assertThatThrownBy(() -> LineDao.findById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 ID에 맞는 노선을 찾지 못했습니다.");
    }
}