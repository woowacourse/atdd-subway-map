package wooteco.subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.MemoryLineDao;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.constant.BlankArgumentException;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineServiceTest {

    private SectionDao sectionDao;
    private LineDao lineDao;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineDao = new MemoryLineDao();
        sectionDao = new MemorySectionDao();
        lineService = new LineService(lineDao, sectionDao);
    }

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600", 1L, 2L, 7);
        assertThat(lineDao.findById(saveLine.getId())).isNotEmpty();
    }

    @DisplayName("지하철 노선 빈 이름으로 저장하면 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyName(String name) {
        assertThatThrownBy(() -> lineService.saveAndGet(name, "bg-red-600", 1L, 2L, 7))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장하면 예외를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        assertThatThrownBy(() -> lineService.saveAndGet("신분당선", color, 1L, 2L, 7))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("중복된 지하철노선 저장")
    @Test
    void saveByDuplicateName() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";

        lineService.saveAndGet(lineName, lineColor, 1L, 2L, 7);

        assertThatThrownBy(() -> lineService.saveAndGet(lineName, lineColor, 1L, 2L, 7))
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
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600", 1L, 2L, 7);

        assertThatThrownBy(() -> lineService.update(saveLine.getId(), name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyColor(String color) {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600", 1L, 2L, 7);

        assertThatThrownBy(() -> lineService.update(saveLine.getId(), "신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선의 정보를 수정한다.")
    @Test
    void updateLine() {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600", 1L, 2L, 7);

        lineService.update(saveLine.getId(), "1호선", "bg-blue-600");

        Line expectedLine = lineDao.findById(saveLine.getId()).orElseThrow();
        assertThat(expectedLine.getName()).isEqualTo("1호선");
        assertThat(expectedLine.getColor()).isEqualTo("bg-blue-600");
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        assertThatThrownBy(() -> lineService.update(50L, "1호선", "bg-red-600"))
                .isInstanceOf(NotExistException.class);
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 예외 반환")
    @Test
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.deleteById(50L))
                .isInstanceOf(NotExistException.class);
    }

    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600", 1L, 2L, 7);

        lineService.deleteById(saveLine.getId());

        assertThat(lineDao.findById(saveLine.getId())).isEmpty();
    }
}
