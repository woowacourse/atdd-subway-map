package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NoSuchLineException;

@Transactional
@JdbcTest
class LineJdbcDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineJdbcDao dao;

    @BeforeEach
    void setUp() {
        dao = new LineJdbcDao(jdbcTemplate);
    }

    @DisplayName("새로운 노선을 저장한다")
    @Test
    void saveLine() {
        // given
        Line line = new Line("line", "color");

        // when
        dao.save(line);

        // then
        List<Line> lines = dao.findAll();
        Line actual = lines.get(0);
        assertThat(actual.getName()).isEqualTo(line.getName());
        assertThat(actual.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("같은 이름의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateName() {
        // given
        dao.save(new Line("line", "color"));

        // when & then
        assertThatThrownBy(() -> dao.save(new Line("line", "color2")))
                .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findAll() {
        // given
        dao.save(new Line("line1", "color1"));
        dao.save(new Line("line2", "color2"));
        dao.save(new Line("line3", "color3"));

        // when
        List<Line> lines = dao.findAll();

        // then
        assertThat(lines).hasSize(3);
    }

    @DisplayName("id로 노선을 조회한다")
    @Test
    void findById() {
        // given
        dao.save(new Line("line1", "color1"));
        Line line = new Line("line2", "color2");
        Long savedId = dao.save(line);

        // when
        Line findLine = dao.findById(savedId);

        // then
        assertThat(findLine.getId()).isEqualTo(savedId);
        assertThat(findLine.getName()).isEqualTo(line.getName());
        assertThat(findLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("존재하지 않는 id로 노선을 조회하면 예외가 발생한다")
    @Test
    void throwExceptionWhenTargetLineDoesNotExist() {
        assertThatThrownBy(() -> dao.findById(1L))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void update() {
        Long savedId = dao.save(new Line("line", "color"));

        Long updateId = dao.update(savedId, "changedName", "changedColor");

        Line findLine = dao.findById(updateId);
        assertThat(findLine.getName()).isEqualTo("changedName");
        assertThat(findLine.getColor()).isEqualTo("changedColor");
    }

    @DisplayName("없는 노선 정보를 변경하려 할 때, 예외를 던진다")
    @Test
    void throwExceptionWhenTryToUpdateNoLine() {
        assertThatThrownBy(() -> dao.update(1L, "changedName", "changedColor"))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        Long savedId = dao.save(new Line("line", "color"));

        // when
        dao.deleteById(savedId);

        // then
        assertThat(dao.findAll()).isEmpty();
    }
}
