package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineDaoTest {

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao();
    }

    @Test
    @DisplayName("Line을 등록할 수 있다.")
    void save() {
        Line line = new Line("신분당선", "bg-red-600");
        Line savedLine = lineDao.save(line);

        assertThat(savedLine.getId()).isNotNull();
    }

    @Test
    @DisplayName("Line을 id로 조회할 수 있다.")
    void findById() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Line findLine = lineDao.findById(line.getId());

        assertThat(findLine).isEqualTo(line);
    }

    @Test
    @DisplayName("Line 전체 조회할 수 있다.")
    void findAll() {
        lineDao.save(new Line("신분당선", "bg-red-600"));
        lineDao.save(new Line("분당선", "bg-green-600"));

        assertThat(lineDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Line을 수정할 수 있다.")
    void update() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        int result = lineDao.update(new Line(line.getId(), "분당선", line.getColor()));

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Line을 삭할 수 있다.")
    void delete() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        int result = lineDao.delete(line.getId());

        assertThat(result).isEqualTo(1);
    }
}
