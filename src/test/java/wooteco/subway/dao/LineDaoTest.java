package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.entity.LineEntity;

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
        LineEntity lineEntity = new LineRequest("신분당선", "bg-red-600")
                .toEntity();
        Line savedLine = lineDao.save(lineEntity);
        assertThat(savedLine.getId()).isNotZero();
    }

    @DisplayName("전체 라인을 조회한다.")
    @Test
    void findAllLines() {
        LineEntity lineEntity = new LineRequest("신분당선", "bg-red-600")
                .toEntity();
        lineDao.save(lineEntity);
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @DisplayName("특정 라인을 조회한다.")
    @Test
    void findById() {
        LineEntity lineEntity = new LineRequest("신분당선", "bg-red-600")
                .toEntity();
        Line savedLine = lineDao.save(lineEntity);
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
        LineEntity lineEntity = new LineRequest("신분당선", "bg-red-600")
                .toEntity();
        Line savedLine = lineDao.save(lineEntity);
        LineEntity updateLineEntity = new LineRequest("경의중앙선", "bg-mint-600")
                .toEntity();
        lineDao.update(savedLine.getId(), updateLineEntity);
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
        LineEntity lineEntity = new LineRequest("신분당선", "bg-red-600")
                .toEntity();
        Line savedLine = lineDao.save(lineEntity);
        lineDao.deleteById(savedLine.getId());
        Optional<Line> wrappedLine = lineDao.findById(savedLine.getId());
        assertThat(wrappedLine).isEmpty();
    }
}
