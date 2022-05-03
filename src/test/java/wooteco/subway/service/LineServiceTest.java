package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;

class LineServiceTest {

    private final LineService lineService = new LineService();

    @Test
    @DisplayName("지하철 노선 추가, 조회, 삭제 테스트")
    void LineCRDTest() {
        lineService.save("line1", "red");
        lineService.save("line2", "yellow");
        lineService.save("line3", "blue");

        List<Line> lines = lineService.findAll();

        assertThat(lines).hasSize(3)
                .extracting("name", "color")
                .containsExactly(tuple("line1", "red"), tuple("line2", "yellow"), tuple("line3", "blue"));

        lineService.delete(lines.get(0).getId());
        lineService.delete(lines.get(1).getId());
        lineService.delete(lines.get(2).getId());

        assertThat(lineService.findAll()).hasSize(0);
    }
}
