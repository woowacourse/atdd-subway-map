package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.jdbc.LineJdbcDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NoSuchLineException;

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
        Line savedLine = dao.save(new Line("line", "color"));
        assertThat(savedLine).isNotNull();
    }

    @DisplayName("null을 입력받는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenSaveNull() {
        assertThatThrownBy(() -> dao.save(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("같은 이름의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateName() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));

        // when & then
        assertThatThrownBy(() -> dao.save(new Line(name, "color2")))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("같은 색깔의 노선이 있는 경우 예외를 던진다")
    @Test
    void throwExceptionWhenHasDuplicateColor() {
        // given
        String color = "red";
        dao.save(new Line("test", color));

        // when & then
        assertThatThrownBy(() -> dao.save(new Line("line", color)))
                .isInstanceOf(DuplicateLineException.class);
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
        Line savedLine = dao.save(new Line("line2", "color2"));

        // when
        Line findLine = dao.findById(savedLine.getId()).get();

        // then
        assertThat(findLine).isEqualTo(savedLine);
    }

    @DisplayName("존재하지 않는 id로 노선을 조회하면 빈 값을 반환한다")
    @Test
    void throwExceptionWhenTargetLineDoesNotExist() {
        Optional<Line> optionalLine = dao.findById(1L);
        assertThat(optionalLine).isEqualTo(Optional.empty());
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void update() {
        Line savedLine = dao.save(new Line("line", "color"));

        dao.update(savedLine.getId(), "changedName", "changedColor");

        Line findLine = dao.findById(savedLine.getId()).get();
        assertThat(findLine).isEqualTo(new Line("changedName", "changedColor"));
    }

    @DisplayName("기존에 존재하는 노선 이름으로 이름을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistName() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));
        Line savedLine = dao.save(new Line("test", "test"));

        //when, then
        assertThatThrownBy(() -> dao.update(savedLine.getId(), name, "changedColor"))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("기존에 존재하는 노선 색깔으로 색깔을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistColor() {
        // given
        String color = "red";
        dao.save(new Line("line", color));
        Line savedLine = dao.save(new Line("duplicateColorLine", "test"));

        //when, then
        assertThatThrownBy(() -> dao.update(savedLine.getId(), "duplicateColorLine", color))
                .isInstanceOf(DuplicateLineException.class);
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
        Line savedLine = dao.save(new Line("line", "color"));

        // when
        dao.deleteById(savedLine.getId());

        // then
        assertThat(dao.findById(savedLine.getId())).isEqualTo(Optional.empty());
    }
}
