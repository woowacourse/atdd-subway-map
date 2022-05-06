package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;

@JdbcTest
class JdbcLineDaoTest {

    private LineDao lineDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
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
    @DisplayName("Line 이름이 존재하는지 확인할 수 있다.")
    void existByName() {
        lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThat(lineDao.existByName("신분당선")).isTrue();
    }

    @Test
    @DisplayName("Line을 수정할 수 있다.")
    void update() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        int result = lineDao.update(new Line(line.getId(), "분당선", line.getColor()));

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Line을 삭제할 수 있다.")
    void delete() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));

        assertThatCode(() -> lineDao.delete(line.getId())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("없는 id의 Line을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId() + 1;

        assertThatThrownBy(() -> lineDao.delete(lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 line 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Line 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId();
        lineDao.delete(lineId);

        assertThatThrownBy(() -> lineDao.delete(lineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 line 입니다.");
    }
}
