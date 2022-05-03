package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @BeforeEach
    void setUp() {
        LineDao.deleteAllLines();
    }

    @DisplayName("라인을 저장한다.")
    @Test
    void lineSaveTest() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        assertThat(savedLine.getId()).isNotZero();
    }

    @DisplayName("전체 라인을 조회한다.")
    @Test
    void findAllLines() {
        LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        assertThat(LineDao.findAllLines()).hasSize(1);
    }

    @DisplayName("특정 라인을 조회한다.")
    @Test
    void findById() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        Optional<Line> wrappedLine = LineDao.findById(savedLine.getId());
        assert (wrappedLine).isPresent();
        assertAll(
                () -> assertThat(wrappedLine.get().getName()).isEqualTo("신분당선"),
                () -> assertThat(wrappedLine.get().getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("특정 라인을 수정한다.")
    @Test
    void updateLine() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        LineDao.updateLine(savedLine.getId(), new Line("경의중앙선", "bg-mint-600"));
        Optional<Line> wrappedLine = LineDao.findById(savedLine.getId());
        assert (wrappedLine).isPresent();
        assertAll(
                () -> assertThat(wrappedLine.get().getName()).isEqualTo("경의중앙선"),
                () -> assertThat(wrappedLine.get().getColor()).isEqualTo("bg-mint-600")
        );
    }

    @DisplayName("특정 라인을 삭제한다.")
    @Test
    void deleteLine() {
        Line savedLine = LineDao.saveLine(new Line("신분당선", "bg-red-600"));
        LineDao.deleteById(savedLine.getId());
        Optional<Line> wrappedLine = LineDao.findById(savedLine.getId());
        assertThat(wrappedLine).isEmpty();
    }
}
