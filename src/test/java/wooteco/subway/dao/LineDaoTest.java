package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Line;

class LineDaoTest {

    private static final String LINE_NAME = "신분당선";
    private static final String LINE_COLOR = "bg-red-600";

    @Test
    @DisplayName("노선을 저장한다.")
    public void save() {
        // given
        LineDao dao = new LineDao();
        Line Line = new Line(LINE_NAME, LINE_COLOR);
        // when
        final Line saved = dao.save(Line);
        // then
        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 예외를 던진다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given
        LineDao dao = new LineDao();
        // when
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        // then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> dao.save(new Line(LINE_NAME, LINE_COLOR)));
    }

    @Test
    @DisplayName("전체 노선을 조회한다.")
    public void findAll() {
        // given
        LineDao dao = new LineDao();
        // when
        List<Line> lines = dao.findAll();
        // then
        assertThat(lines).hasSize(0);
    }

    @Test
    @DisplayName("노선을 하나 추가한 뒤, 전체 노선을 조회한다")
    public void findAll_afterSaveOneLine() {
        // given
        LineDao dao = new LineDao();
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
        LineDao dao = new LineDao();
        final Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR));
        // when
        final Line found = dao.findById(saved.getId());
        // then
        Assertions.assertAll(
            () -> assertThat(found.getId()).isEqualTo(saved.getId()),
            () -> assertThat(found.getName()).isEqualTo(saved.getName()),
            () -> assertThat(found.getColor()).isEqualTo(saved.getColor())
        );
    }

    @Test
    @DisplayName("존재하지 않는 ID 값으로 노선을 조회하면 예외를 던진다")
    public void findById_invalidID() {
        // given & when
        LineDao dao = new LineDao();
        dao.save(new Line(LINE_NAME, LINE_COLOR));
        // then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> dao.findById(2L));
    }

    @Test
    @DisplayName("노선 정보를 수정한다.")
    public void update() {
        // given & when
        LineDao dao = new LineDao();
        final Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR));
        // then
        assertThatNoException()
            .isThrownBy(() -> dao.update(new Line(saved.getId(), "구분당선", LINE_COLOR)));
    }

    @Test
    @DisplayName("존재하지 않는 ID값을 수정하는 경우 예외를 던진다.")
    public void update_throwsExceptionWithInvalidId() {
        // given
        LineDao dao = new LineDao();
        final Line saved = dao.save(new Line(LINE_NAME, LINE_COLOR));
        // when
        Line updateLine = new Line(100L, "사랑이넘치는", "우테코");
        // then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> dao.update(updateLine));
    }
}