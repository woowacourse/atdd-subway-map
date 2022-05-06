package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

public class LineDaoTest extends DaoTest {

    @Test
    @DisplayName("노선을 저장하면 저장된 노선 정보를 반환한다.")
    void Save() {
        // given
        final Line line = new Line("7호선", "bg-red-600");

        // when
        final Line savedLine = lineDao.save(line).orElseThrow();

        // then
        assertThat(savedLine.getName()).isEqualTo(line.getName());
        assertThat(savedLine.getColor()).isEqualTo(line.getColor());
    }

    @Test
    @DisplayName("저장하려는 노선 이름이 중복이면 예외를 던진다.")
    void Save_DuplicateName_ExceptionThrown() {
        // given
        final String name = "7호선";

        lineDao.save(new Line(name, "bg-red-600")).orElseThrow();

        // when
        final Line line = new Line(name, "bg-blue-600");

        // then
        final Optional<Line> possibleLine = lineDao.save(line);
        assertThat(possibleLine).isEmpty();
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void FindAll() {
        // given
        final Line line7 = new Line("7호선", "bg-red-600");
        lineDao.save(line7);

        final String line5Name = "5호선";
        final String line5Color = "bg-green-600";
        final Line line5 = new Line(line5Name, line5Color);
        lineDao.save(line5);

        // when
        final List<Line> lines = lineDao.findAll();

        // then
        assertThat(lines.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 조회한다.")
    void FindById() {
        // given
        final Line line7 = new Line("7호선", "bg-red-600");
        final Line persistLine = lineDao.save(line7).orElseThrow();

        // when
        final Optional<Line> actual = lineDao.findById(persistLine.getId());

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(persistLine);
    }

    @Test
    @DisplayName("존재하지 않은 id로 노선을 조회하면 Empty Optional을 반환한다.")
    void FindById_InvalidId_EmptyOptional() {
        // when
        final Optional<Line> possibleLine = lineDao.findById(1L);

        // then
        assertThat(possibleLine.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 업데이트한다.")
    void UpdateById() {
        // given
        final Line persistLine = lineDao.save(new Line("7호선", "bg-red-600")).orElseThrow();

        // when
        final Line line = new Line("5호선", "bg-green-600");
        final Line updatedLine = lineDao.updateById(persistLine.getId(), line).orElseThrow();

        // then
        assertThat(updatedLine).isEqualTo(line);
    }

    @Test
    @DisplayName("업데이트하려는 노선 이름이 중복되면 에외가 발생한다.")
    void UpdateById_InvalidId_ExceptionThrown() {
        // given
        final String name = "5호선";

        // when
        final Line line = new Line(name, "bg-green-600");

        // then
        assertThatThrownBy(() -> lineDao.updateById(999L, line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id가 일치하는 노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("id를 통해 해당하는 노선을 삭제한다.")
    void DeleteById() {
        // given
        final Line persistLine = lineDao.save(new Line("7호선", "bg-red-600")).orElseThrow();

        // when
        final Long id = persistLine.getId();
        final Integer affectedRows = lineDao.deleteById(id);

        // then
        assertThat(affectedRows).isOne();
        assertThat(lineDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("존재하지 않는 id의 역을 삭제하면 예외가 발생한다.")
    void DeleteById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> lineDao.deleteById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id가 일치하는 노선이 존재하지 않습니다.");
    }
}
