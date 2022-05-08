package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class LineDaoTest {

    private final LineDao lineDao;

    @Autowired
    LineDaoTest(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.lineDao = new LineDao(namedParameterJdbcTemplate);
    }

    @DisplayName("라인을 저장한다.")
    @Test
    void lineSaveTest() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        assertThat(savedLine.getId()).isNotZero();
    }

    @DisplayName("전체 라인을 조회한다.")
    @Test
    void findAllLines() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @DisplayName("특정 라인을 조회한다.")
    @Test
    void findById() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        Optional<Line> wrappedLine = lineDao.findById(savedLine.getId());
        assert (wrappedLine).isPresent();
        assertAll(
                () -> assertThat(wrappedLine.get().getName()).isEqualTo("신분당선"),
                () -> assertThat(wrappedLine.get().getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("특정 라인을 수정한다.")
    @Test
    void updateLine() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.update(savedLine.getId(), new Line("경의중앙선", "bg-mint-600"));
        Optional<Line> wrappedLine = lineDao.findById(savedLine.getId());
        assert (wrappedLine).isPresent();
        assertAll(
                () -> assertThat(wrappedLine.get().getName()).isEqualTo("경의중앙선"),
                () -> assertThat(wrappedLine.get().getColor()).isEqualTo("bg-mint-600")
        );
    }

    @DisplayName("특정 라인을 삭제한다.")
    @Test
    void deleteLine() {
        Line savedLine = lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.deleteById(savedLine.getId());
        Optional<Line> wrappedLine = lineDao.findById(savedLine.getId());
        assertThat(wrappedLine).isEmpty();
    }
}
