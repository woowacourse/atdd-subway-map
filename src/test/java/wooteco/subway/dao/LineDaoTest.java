package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red-600";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineDao dao;

    @BeforeEach
    void setUp() {
        dao = new JdbcLineDao(dataSource, jdbcTemplate);
    }

    @Test
    @DisplayName("노선을 저장한다.")
    public void save() {
        // given
        Line Line = new Line(LINE_NAME, LINE_COLOR);
        // when
        final Optional<Line> saved = dao.save(Line);
        // then
        assertThat(saved).isPresent();
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 빈 Optional을 돌려준다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given
        final Optional<Line> saved = dao.save(new Line(LINE_NAME, LINE_COLOR));
        // when
        final Optional<Line> duplicated = dao.save(new Line(LINE_NAME, LINE_COLOR));
        // then
        assertThat(duplicated).isEmpty();
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    public void findAll() {
        // given & when
        List<Line> lines = dao.findAll();
        // then
        assertThat(lines).hasSize(0);
    }

    @Test
    @DisplayName("노선을 하나 추가한 뒤, 전체 노선을 조회한다")
    public void findAll_afterSaveOneLine() {
        // given
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        // when
        List<Line> lines = dao.findAll();
        // then
        assertThat(lines).hasSize(1);
    }

    @Test
    @DisplayName("ID 값으로 노선을 조회한다")
    public void findById() {
        // given
        final Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR)).orElseThrow(IllegalStateException::new);
        // when
        final Optional<Line> foundLine = dao.findById(saved.getId());
        // then
        assertThat(foundLine).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 ID 값으로 노선을 조회하면 빈 Optional을 돌려준다.")
    public void findById_invalidID() {
        // given
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        // when
        final Optional<Line> found = dao.findById(2L);
        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    public void update() {
        // given
        final Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR)).orElseThrow(IllegalStateException::new);
        // when
        final boolean isUpdated = dao.update(new Line(saved.getId(), "구분당선", LINE_COLOR));
        // then
        assertThat(isUpdated).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 ID값을 수정하는 경우 False를 반환한다.")
    public void update_throwsExceptionWithInvalidId() {
        // given
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        Line updateLine = new Line(100L, "사랑이넘치는", "우테코");
        // when
        final boolean isUpdated = dao.update(updateLine);
        // then
        assertThat(isUpdated).isFalse();
    }

    @Test
    @DisplayName("ID값으로 노선을 삭제한다.")
    public void delete() {
        // given
        Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR)).orElseThrow(IllegalStateException::new);
        // when
        final boolean isDeleted = dao.delete(saved.getId());
        // then
        assertThat(isDeleted).isTrue();
    }

    @Test
    @DisplayName("존재하지않는 ID값을 삭제하는 경우 False를 반환한다.")
    public void delete_throwsExceptionWithInvalidId() {
        // given
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        Long deleteId = 100L;
        // when
        final boolean isDeleted = dao.delete(deleteId);
        // then
        assertThat(isDeleted).isFalse();
    }
}