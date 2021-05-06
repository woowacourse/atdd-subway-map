package wooteco.subway.line.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.DuplicateLineException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.line.Line;

class LineDaoCacheTest {

    private static final LineDaoCache lineDaoCache = new LineDaoCache();
    private static Line line;

    @BeforeEach
    public void setTest() {
        lineDaoCache.clean();
        line = lineDaoCache.create(new Line("1호선", "파란색"));
    }

    @DisplayName("노선 저장")
    @Test
    public void save() {
        // given
        Line line = new Line("10호선", "붉은색");

        // when
        Line requestedLine = lineDaoCache.create(line);

        // then
        assertThat(requestedLine.getName()).isEqualTo(line.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("노선 중복 저장 시도")
    @Test
    public void duplicatedSaveException() {
        // given
        Line line1 = new Line("1호선", "초록색");
        Line line2 = new Line("2호선", "파란색");

        // when

        // then
        assertThatThrownBy(() -> lineDaoCache.create(line1))
            .isInstanceOf(DuplicateLineException.class);

        assertThatThrownBy(() -> lineDaoCache.create(line2))
            .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("id값에 맞는 노선 반환")
    @Test
    public void findLine() {
        // given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = lineDaoCache.create(line1);
        long id = saveLine.getId();

        // when
        Line requestedLine = lineDaoCache.show(id);

        // then
        assertThat(requestedLine.getId()).isEqualTo(id);
        assertThat(requestedLine.getName()).isEqualTo(line1.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line1.getColor());
    }

    @DisplayName("존재하지 않는 id 값을 가진 노선 반환 시도")
    @Test
    void findLineNotFoundLineException() {
        // given

        // when
        long id = -1;

        // then
        assertThatThrownBy(() -> lineDaoCache.show(id))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("모든 노선 호출")
    @Test
    void findAll() {
        // given

        // when
        List<Line> lines = lineDaoCache.showAll();

        // then
        assertThat(lines.get(0)).isEqualTo(line);
    }

    @DisplayName("노선 업데이트")
    @Test
    void update() {
        // given
        Line line1 = new Line("11호선", "보라색");
        Line saveLine = lineDaoCache.create(line1);
        long id = saveLine.getId();
        String requestName = "분당선";
        String requestColor = "노란색";
        Line requestLine = new Line(requestName, requestColor);

        // when
        lineDaoCache.update((int) id, requestLine);
        Line responseLine = lineDaoCache.show(id);

        // then
        assertThat(responseLine.getName()).isEqualTo(requestName);
        assertThat(responseLine.getColor()).isEqualTo(requestColor);
    }

    @DisplayName("노선 삭제")
    @Test
    void remove() {
        // given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = lineDaoCache.create(line1);
        long id = saveLine.getId();

        // when
        lineDaoCache.delete(id);

        // then
        assertThatThrownBy(() -> lineDaoCache.show(id))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("노선 삭제시도 중 해당 노선을 찾지 못할 경우 예외처리")
    @Test
    void removeNotFoundException() {
        // given
        long id = -1;

        // when

        // then
        assertThatThrownBy(() -> lineDaoCache.delete(id))
            .isInstanceOf(NotFoundLineException.class);
    }
}