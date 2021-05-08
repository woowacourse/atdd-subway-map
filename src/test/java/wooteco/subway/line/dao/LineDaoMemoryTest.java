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

class LineDaoMemoryTest {

    private static final LineDaoMemory LINE_DAO_MEMORY = new LineDaoMemory();
    private static Line line;

    @BeforeEach
    public void setTest() {
        LINE_DAO_MEMORY.clean();
        line = LINE_DAO_MEMORY.create(new Line("1호선", "파란색"));
    }

    @DisplayName("노선 저장")
    @Test
    public void save() {
        //given
        Line line = new Line("10호선", "붉은색");

        //when
        Line requestedLine = LINE_DAO_MEMORY.create(line);

        //then
        assertThat(requestedLine.getName()).isEqualTo(line.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("노선 중복 저장 시도")
    @Test
    public void duplicatedSave() {
        //given
        Line line1 = new Line("1호선", "초록색");
        Line line2 = new Line("2호선", "파란색");

        //then
        assertThatThrownBy(() -> LINE_DAO_MEMORY.create(line1))
            .isInstanceOf(DuplicateLineException.class);

        assertThatThrownBy(() -> LINE_DAO_MEMORY.create(line2))
            .isInstanceOf(DuplicateLineException.class);
    }

    @DisplayName("id값에 맞는 노선 반환")
    @Test
    public void findLine() {
        //given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = LINE_DAO_MEMORY.create(line1);
        long id = saveLine.getId();

        //when
        Line requestedLine = LINE_DAO_MEMORY.show(id);

        //then
        assertThat(requestedLine.getId()).isEqualTo(id);
        assertThat(requestedLine.getName()).isEqualTo(line1.getName());
        assertThat(requestedLine.getColor()).isEqualTo(line1.getColor());
    }

    @DisplayName("존재하지 않는 id 값을 가진 노선 반환 시도")
    @Test
    void findLineVoidId() {
        //given

        //when
        long id = -1;

        //then
        assertThatThrownBy(() -> LINE_DAO_MEMORY.show(id))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("전 노선 호출")
    @Test
    void findAll() {
        //given

        //when
        List<Line> lines = LINE_DAO_MEMORY.showAll();

        //then
        assertThat(lines.get(0)).isEqualTo(line);
    }

    @DisplayName("노선 업데이트")
    @Test
    void update() {
        //given
        Line line1 = new Line("11호선", "보라색");
        Line saveLine = LINE_DAO_MEMORY.create(line1);
        long id = saveLine.getId();
        String requestName = "분당선";
        String requestColor = "노란색";
        Line requestLine = new Line(requestName, requestColor);

        //when
        LINE_DAO_MEMORY.update((int) id, requestLine);
        Line responseLine = LINE_DAO_MEMORY.show(id);

        //then
        assertThat(responseLine.getName()).isEqualTo(requestName);
        assertThat(responseLine.getColor()).isEqualTo(requestColor);
    }

    @DisplayName("노선 삭제")
    @Test
    void remove() {
        //given
        Line line1 = new Line("12호선", "분홍색");
        Line saveLine = LINE_DAO_MEMORY.create(line1);
        long id = saveLine.getId();

        //when
        LINE_DAO_MEMORY.delete(id);

        //then
        assertThatThrownBy(() -> LINE_DAO_MEMORY.show(id))
            .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("노선 삭제 실패")
    @Test
    void removeFail() {
        //given
        long id = -1;

        //when

        //then
        assertThatThrownBy(() -> LINE_DAO_MEMORY.delete(id))
            .isInstanceOf(NotFoundLineException.class);
    }
}