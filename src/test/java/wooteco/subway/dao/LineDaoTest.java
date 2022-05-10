package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
        LineEntity lineEntity = new LineEntity.Builder("신분당선", "bg-red-600")
                .build();
        LineEntity saveLineEntity = lineDao.save(lineEntity);
        assertThat(saveLineEntity.getId()).isNotZero();
    }

    @DisplayName("전체 라인을 조회한다.")
    @Test
    void findAllLines() {
        LineEntity lineEntity = new LineEntity.Builder("신분당선", "bg-red-600")
                .build();
        lineDao.save(lineEntity);
        assertThat(lineDao.findAll()).hasSize(1);
    }

    @DisplayName("특정 라인을 조회한다.")
    @Test
    void findById() {
        LineEntity lineEntity = new LineEntity.Builder("신분당선", "bg-red-600")
                .build();
        LineEntity saveLineEntity = lineDao.save(lineEntity);
        Optional<LineEntity> wrappedLineEntity = lineDao.findById(saveLineEntity.getId());
        assert (wrappedLineEntity).isPresent();
        assertAll(
                () -> assertThat(wrappedLineEntity.get().getName()).isEqualTo("신분당선"),
                () -> assertThat(wrappedLineEntity.get().getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("특정 라인을 수정한다.")
    @Test
    void updateLine() {
        LineEntity lineEntity = new LineEntity.Builder("신분당선", "bg-red-600")
                .build();
        LineEntity saveLineEntity = lineDao.save(lineEntity);
        LineEntity updateLineEntity = new LineEntity.Builder("경의중앙선", "bg-mint-600")
                .build();

        lineDao.update(saveLineEntity.getId(), updateLineEntity);

        Optional<LineEntity> wrappedLine = lineDao.findById(saveLineEntity.getId());
        assert (wrappedLine).isPresent();
        assertAll(
                () -> assertThat(wrappedLine.get().getName()).isEqualTo("경의중앙선"),
                () -> assertThat(wrappedLine.get().getColor()).isEqualTo("bg-mint-600")
        );
    }

    @DisplayName("특정 라인을 삭제한다.")
    @Test
    void deleteLine() {
        LineEntity lineEntity = new LineEntity.Builder("신분당선", "bg-red-600")
                .build();
        LineEntity saveLineEntity = lineDao.save(lineEntity);

        lineDao.deleteById(saveLineEntity.getId());
        Optional<LineEntity> wrappedLine = lineDao.findById(saveLineEntity.getId());
        assertThat(wrappedLine).isEmpty();
    }
}
