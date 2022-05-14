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
class LineDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao dao;

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
        // given
        Line savedLine = dao.save(new Line("line", "color"));

        // when
        dao.update(new Line(savedLine.getId(), "changedName", "changedColor"));

        // then
        Line findLine = dao.findById(savedLine.getId()).get();
        assertThat(findLine.getName()).isEqualTo("changedName");
        assertThat(findLine.getColor()).isEqualTo("changedColor");
    }

    @DisplayName("기존에 존재하는 노선 이름으로 이름을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistName() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));
        Line savedLine = dao.save(new Line("test", "test"));

        //when, then
        assertThatThrownBy(() -> dao.update(new Line(savedLine.getId(), name, "changedColor")))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("기존에 존재하는 노선 색깔으로 색깔을 수정하면 예외가 발생한다")
    @Test
    void throwExceptionWhenUpdateToExistColor() {
        // given
        String color = "red";
        dao.save(new Line("line", color));
        Line savedLine = dao.save(new Line("test", "test"));

        //when, then
        assertThatThrownBy(() -> dao.update(new Line(savedLine.getId(), "changedName", color)))
                .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("없는 노선 정보를 변경하려 할 때, 예외를 던진다")
    @Test
    void throwExceptionWhenTryToUpdateNoLine() {
        Line nonExistLine = dao.save(new Line("test", "test"));
        dao.deleteById(nonExistLine.getId());

        assertThatThrownBy(() -> dao.update(new Line(nonExistLine.getId(), "changedName", "changedColor")))
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

    @DisplayName("입력된 이름을 가진 노선이 존재하면 참을 반환한다.")
    @Test
    void existByNameReturnTrue() {
        // given
        String name = "line";
        dao.save(new Line(name, "color"));

        // when
        boolean actual = dao.existByName(name);

        // then
        assertThat(actual).isEqualTo(true);
    }

    @DisplayName("입력된 이름을 가진 노선이 존재하지 않으면 거짓을 반환한다.")
    @Test
    void existByNameReturnFalse() {
        // when
        boolean actual = dao.existByName("nonExist");

        // then
        assertThat(actual).isEqualTo(false);
    }

    @DisplayName("입력된 색깔을 가진 노선이 존재하면 참을 반환한다.")
    @Test
    void existByColorReturnTrue() {
        // given
        String color = "color";
        dao.save(new Line("line", color));

        // when
        boolean actual = dao.existByColor(color);

        // then
        assertThat(actual).isEqualTo(true);
    }

    @DisplayName("입력된 색깔을 가진 노선이 존재하지 않으면 거짓을 반환한다.")
    @Test
    void existByColorReturnFalse() {
        // when
        boolean actual = dao.existByColor("nonExist");

        // then
        assertThat(actual).isEqualTo(false);
    }
}
