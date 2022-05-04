package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.BlankArgumentException;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistException;

public class LineServiceTest {

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService();
        LineDao.deleteAll();
    }

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        Line line = lineService.save("신분당선", "bg-red-600");

        assertThat(LineDao.findById(line.getId())).isNotEmpty();
    }

    @DisplayName("지하철 노선 빈 이름으로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyName(String name) {
        assertThatThrownBy(() -> lineService.save(name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        assertThatThrownBy(() -> lineService.save("신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("중복된 지하철노선 저장")
    @Test
    void saveByDuplicateName() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";

        lineService.save(lineName, lineColor);

        assertThatThrownBy(() -> lineService.save(lineName, lineColor))
            .isInstanceOf(DuplicateException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        assertThatThrownBy(() -> lineService.findById(50L))
            .isInstanceOf(NotExistException.class);
    }

    @DisplayName("지하철 노선 빈 이름으로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyName(String name) {
        Line line = lineService.save("신분당선", "bg-red-600");

        assertThatThrownBy(() -> lineService.update(line.getId(), name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyColor(String color) {
        Line line = lineService.save("신분당선", "bg-red-600");

        assertThatThrownBy(() -> lineService.update(line.getId(), "신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }

    @Test
    void updateLine() {
        Line line = lineService.save("신분당선", "bg-red-600");

        lineService.update(line.getId(), "1호선", "bg-blue-600");

        Line expectedLine = LineDao.findById(line.getId()).orElseThrow();
        assertThat(expectedLine.getName()).isEqualTo("1호선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-blue-600");
    }
}
