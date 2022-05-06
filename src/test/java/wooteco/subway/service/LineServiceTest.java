package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wooteco.subway.service.LineService.DUPLICATE_EXCEPTION_MESSAGE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.mock.MemoryLineDao;

class LineServiceTest {
    private MemoryLineDao lineDao = new MemoryLineDao();
    private LineService lineService = new LineService(lineDao);

    @BeforeEach
    void beforeEach() {
        lineDao.clear();
    }

    @Test
    void saveDuplicateName() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color));

        assertThatThrownBy(() -> lineService.create(new Line(name, "blue")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    void saveDuplicateColor() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color));

        assertThatThrownBy(() -> lineService.create(new Line("2호선", color)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }

    @Test
    void updateDuplicate() {
        String name = "신분당선";
        String color = "bg-red-600";
        lineService.create(new Line(name, color));

        Line line = lineService.create(new Line("2호선", "bg-green-600"));

        Line duplicateUpdate = new Line(line.getId(), name, color);
        assertThatThrownBy(() -> lineService.update(duplicateUpdate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DUPLICATE_EXCEPTION_MESSAGE);
    }
}
