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
class LineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("저장을 하고 리스트 및 단일 조회를 할 수 있다")
    @Test
    void can_save_find_findAll() {
        List<Line> linesBeforeSave = lineDao.findAll();
        assertThat(linesBeforeSave.size()).isEqualTo(0);

        Line line = new Line(1L, "선릉역", "bg-yellow-600");
        Line savedLine = lineDao.save(line);

        List<Line> linesAfterSave = lineDao.findAll();
        assertThat(linesAfterSave.size()).isEqualTo(1);

        Long savedLineId = savedLine.getId();
        Line foundLine = lineDao.findById(savedLineId).get();
        assertThat(foundLine).isEqualTo(new Line(savedLineId, "선릉역", "bg-yellow-600"));
    }

    @DisplayName("수정을 할 수 있다")
    @Test
    void can_update() {
        Line line = new Line(1L, "선릉역", "bg-yellow-600");
        lineDao.save(line);

        lineDao.update(new Line(1L, "서울역", "bg-blue-600"));

        Line foundLine = lineDao.findById(1L).get();
        assertThat(foundLine.getName()).isEqualTo("서울역");
    }
}