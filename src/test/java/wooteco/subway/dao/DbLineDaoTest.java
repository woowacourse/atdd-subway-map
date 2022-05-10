package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class DbLineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DbLineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new DbLineDao(jdbcTemplate);
    }

    @DisplayName("저장을 하고 리스트 및 단일 조회를 할 수 있다")
    @Test
    void can_save_find_findAll() {
        List<Line> linesBeforeSave = lineDao.findAll();
        assertThat(linesBeforeSave.size()).isEqualTo(0);

        Line line = new Line("신분당선", "bg-yellow-600");
        long savedLineId = lineDao.save(line);

        List<Line> linesAfterSave = lineDao.findAll();
        assertThat(linesAfterSave.size()).isEqualTo(1);

        Line foundLine = lineDao.findById(savedLineId).get();
        assertThat(foundLine).isEqualTo(new Line(savedLineId, "신분당선", "bg-yellow-600"));
    }

    @DisplayName("수정을 할 수 있다")
    @Test
    void can_update() {
        Line line = new Line("신분당선", "bg-yellow-600");
        long savedLineId = lineDao.save(line);

        lineDao.update(new Line(savedLineId, "7호선", "bg-brown-600"));

        Line foundLine = lineDao.findById(savedLineId).get();
        assertThat(foundLine.getName()).isEqualTo("7호선");
    }

    @DisplayName("삭제를 할 수 있다")
    @Test
    void can_delete() {
        Line line = new Line( "신분당선", "bg-yellow-600");
        long savedLineId = lineDao.save(line);

        lineDao.deleteById(savedLineId);

        List<Line> foundLines = lineDao.findAll();
        assertThat(foundLines.size()).isEqualTo(0);
    }

    @DisplayName("전체 삭제를 할 수 있다")
    @Test
    void can_deleteAll() {
        lineDao.save(new Line("신분당선", "bg-yellow-600"));
        lineDao.save(new Line("7호선", "bg-brown-600"));

        lineDao.deleteAll();

        List<Line> foundLines = lineDao.findAll();
        assertThat(foundLines.size()).isEqualTo(0);
    }
}