package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;

@DisplayName("Line Service")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class LineServiceTest {

    private final LineService lineService;

    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        Line line = new Line("2호선", "bg-green-600");
        Line result = lineService.createLine(line);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(line.getName());
        assertThat(result.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("중복된 이름을 갖는 노선을 생성하면, 예외가 발생한다.")
    @Test
    void createDuplicateLineException() {
        Line line = new Line("2호선", "bg-green-600");
        lineService.createLine(line);

        assertThatThrownBy(() -> lineService.createLine(line))
            .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        lineService.createLine(new Line("2호선", "bg-green-600"));
        lineService.createLine(new Line("3호선", "bg-orange-600"));
        Line line = lineService.showLine(1L);

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("없는 노선을 조회하면, 예외가 발생한다.")
    @Test
    void showNotExistLineException() {
        assertThatThrownBy(() -> lineService.showLine(1L))
            .isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void showLines() {
        Line line2 = lineService.createLine(new Line("2호선", "bg-green-600"));
        Line line3 = lineService.createLine(new Line("3호선", "bg-orange-600"));
        Line line4 = lineService.createLine(new Line("4호선", "bg-skyBlue-600"));

        List<Line> lines = lineService.showLines();

        assertThat(lines).hasSize(3);
        assertThat(lines).containsExactly(line2, line3, line4);
    }

    @DisplayName("조회되는 노선 목록이 없으면, 예외가 발생한다.")
    @Test
    void showNotExistLinesException() {
        assertThatThrownBy(lineService::showLines).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line line2 = new Line("2호선", "bg-green-600");
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        lineService.createLine(line2);
        lineService.updateLine(newLine);

        assertThat(lineService.showLine(1L)).isEqualTo(newLine);
    }

    @DisplayName("없는 노선을 수정하면, 예외가 발생한다.")
    @Test
    void updateException() {
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        assertThatThrownBy(() -> {
            lineService.updateLine(newLine);
        }).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Line line2 = new Line("2호선", "bg-green-600");

        lineService.createLine(line2);

        assertThatCode(() -> lineService.deleteLine(1L)).doesNotThrowAnyException();
        assertThatThrownBy(lineService::showLines).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("없는 노선을 삭제하면, 예외가 발생한다.")
    @Test
    void deleteException() {
        assertThatThrownBy(() -> lineService.deleteLine(1L))
            .isInstanceOf(NotExistLineException.class);
    }

}
