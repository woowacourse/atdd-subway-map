package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.entity.LineEntity;

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
        LineEntity entity = new LineEntity(LINE_NAME, LINE_COLOR);
        // when
        final Long id = dao.save(entity);
        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 예외가 발생한다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given
        LineEntity entity = new LineEntity(LINE_NAME, LINE_COLOR);
        // when
        dao.save(entity);
        // then
        assertThatExceptionOfType(DuplicateKeyException.class)
            .isThrownBy(() -> dao.save(entity));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    public void findAll() {
        // given & when
        List<LineEntity> lines = dao.findAll();
        // then
        assertThat(lines).hasSize(0);
    }

    @Test
    @DisplayName("노선을 하나 추가한 뒤, 전체 노선을 조회한다")
    public void findAll_afterSaveOneLine() {
        // given
        dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        // when
        List<LineEntity> lines = dao.findAll();
        // then
        assertThat(lines).hasSize(1);
    }

    @Test
    @DisplayName("ID 값으로 노선을 조회한다")
    public void findById() {
        // given
        final Long id = dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        // when
        final Optional<LineEntity> foundLine = dao.findById(id);
        // then
        assertThat(foundLine).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 ID 값으로 노선을 조회하면 빈 Optional을 돌려준다.")
    public void findById_invalidID() {
        // given
        dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        // when
        final Optional<LineEntity> found = dao.findById(2L);
        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    public void update() {
        // given
        final Long id = dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        // when
        final Long updatedId = dao.update(new LineEntity(id, "구분당선", LINE_COLOR));
        // then
        assertThat(updatedId).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 ID값을 수정하는 경우 null을 반환한다.")
    public void update_throwsExceptionWithInvalidId() {
        // given
        dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        LineEntity updateLine = new LineEntity(100L, "사랑이넘치는", "우테코");
        // when
        final Long id = dao.update(updateLine);
        // then
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("ID값으로 노선을 삭제한다.")
    public void delete() {
        // given
        final Long id = dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        // when
        final Long deletedId = dao.delete(id);
        // then
        assertThat(deletedId).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지않는 ID값을 삭제하는 경우 null을 반환한다.")
    public void delete_throwsExceptionWithInvalidId() {
        // given
        dao.save(new LineEntity(LINE_NAME, LINE_COLOR));
        Long deleteId = 100L;
        // when
        final Long deletedId = dao.delete(deleteId);
        // then
        assertThat(deletedId).isNull();
    }
}