package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

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


}