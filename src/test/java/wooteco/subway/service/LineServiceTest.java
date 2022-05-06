package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Line;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("이미 존재하는 노선의 이름이 있을 때 예외가 발생한다.")
    void saveExceptionByExistName() {
        lineService.save(new Line("신분당선", "bg-red-600"));
        assertThatThrownBy(() -> lineService.save(new Line("신분당선", "bg-green-600")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 노선 이름입니다.");
    }

    @Test
    @DisplayName("없는 id의 Line을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Line line = lineService.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId() + 1;

        assertThatThrownBy(() -> lineService.delete(lineId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Line 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Line을 또 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Line line = lineService.save(new Line("신분당선", "bg-red-600"));
        Long lineId = line.getId();
        lineService.delete(lineId);

        assertThatThrownBy(() -> lineService.delete(lineId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Line 입니다.");
    }
}
