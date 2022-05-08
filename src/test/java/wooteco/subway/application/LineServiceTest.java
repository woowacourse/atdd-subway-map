package wooteco.subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.exception.constant.BlankArgumentException;
import wooteco.subway.exception.constant.DuplicateException;
import wooteco.subway.exception.constant.NotExistException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @DisplayName("지하철 노선 저장")
    @Test
    void saveLine() {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600");

        assertThat(lineDao.findById(saveLine.getId())).isNotEmpty();
    }

    @DisplayName("지하철 노선 빈 이름으로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyName(String name) {
        assertThatThrownBy(() -> lineService.saveAndGet(name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 저장")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveLineWithEmptyColor(String color) {
        assertThatThrownBy(() -> lineService.saveAndGet("신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("중복된 지하철노선 저장")
    @Test
    void saveByDuplicateName() {
        String lineName = "신분당선";
        String lineColor = "bg-red-600";

        lineService.saveAndGet(lineName, lineColor);

        assertThatThrownBy(() -> lineService.saveAndGet(lineName, lineColor))
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
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600");

        assertThatThrownBy(() -> lineService.update(saveLine.getId(), name, "bg-red-600"))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선 빈 색깔로 수정")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void updateLineWithEmptyColor(String color) {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600");

        assertThatThrownBy(() -> lineService.update(saveLine.getId(), "신분당선", color))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 노선의 정보를 수정한다.")
    @Test
    void updateLine() {
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600");

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
        Line saveLine = lineService.saveAndGet("신분당선", "bg-red-600");

        lineService.deleteById(saveLine.getId());

        assertThat(lineDao.findById(saveLine.getId())).isEmpty();
    }
}
