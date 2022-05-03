package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

public class LineDaoTest {

    @AfterEach
    void rollback() {
        LineDao.findAll().clear();
    }

    @Test
    @DisplayName("지하철 노선을 저장한다.")
    void save() {
        final Line line = new Line("신분당선", "bg-red-600");

        final Line savedLine = LineDao.save(line);

        assertAll(() -> {
            assertThat(savedLine.getId()).isNotNull();
            assertThat(savedLine.getName()).isEqualTo(line.getName());
            assertThat(savedLine.getColor()).isEqualTo(line.getColor());
        });
    }

    @Test
    @DisplayName("같은 이름의 노선을 저장하는 경우, 예외가 발생한다.")
    void saveDuplicateName() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("신분당선", "bg-blue-700");
        LineDao.save(line1);

        assertThatThrownBy(() -> LineDao.save(line2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선이 이미 존재합니다.");
    }

    @Test
    @DisplayName("같은 색상의 노선을 저장하는 경우, 예외가 발생한다.")
    void saveDuplicateColor() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-red-600");
        LineDao.save(line1);

        assertThatThrownBy(() -> LineDao.save(line2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 색상의 노선이 이미 존재합니다.");
    }

    @Test
    @DisplayName("모든 지하철 노선을 조회한다.")
    void findAll() {
        final Line line1 = new Line("신분당선", "bg-red-600");
        final Line line2 = new Line("분당선", "bg-black-000");
        LineDao.save(line1);
        LineDao.save(line2);

        final List<Line> lines = LineDao.findAll();

        assertThat(lines).hasSize(2);
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void find() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Line savedLine = LineDao.save(line);

        final Line foundLine = LineDao.find(savedLine.getId());

        assertAll(() -> {
            assertThat(foundLine.getName()).isEqualTo(savedLine.getName());
            assertThat(foundLine.getColor()).isEqualTo(savedLine.getColor());
        });
    }

    @Test
    @DisplayName("존재하지 않는 Id 조회 시, 예외를 발생한다.")
    void findNotExistId() {
        final long id = 1L;

        assertThatThrownBy(() -> LineDao.find(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @Test
    @DisplayName("노선 정보를 업데이트 한다.")
    void update() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Long id = LineDao.save(line).getId();

        final String updateName = "분당선";
        String updateColor = "bg-blue-900";
        final Line updatedLine = LineDao.update(id, updateName, updateColor);

        assertAll(() -> {
            assertThat(updatedLine.getName()).isEqualTo(updateName);
            assertThat(updatedLine.getColor()).isEqualTo(updateColor);
        });
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        final Line line = new Line("신분당선", "bg-red-600");
        final Long id = LineDao.save(line).getId();

        LineDao.delete(id);

        assertThat(LineDao.findAll()).hasSize(0);
    }
}
