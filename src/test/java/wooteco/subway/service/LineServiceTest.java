package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    @DisplayName("중복된 노선 이름 입력 시 예외 발생 테스트")
    void validateDuplicationNameTest() {
        lineService.save("line1", "red");

        assertThatThrownBy(() -> lineService.save("line1", "yellow"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 노선 이름입니다.");

        List<Line> lines = lineService.findAll();
        lineService.delete(lines.get(0).getId());
    }

    @Test
    @DisplayName("중복된 노선 색깔 입력 시 예외 발생 테스트")
    void validateDuplicationColorTest() {
        lineService.save("line1", "red");

        assertThatThrownBy(() -> lineService.save("line2", "red"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 노선 색깔입니다.");

        List<Line> lines = lineService.findAll();
        lineService.delete(lines.get(0).getId());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateTest() {
        lineService.save("line1", "red");
        List<Line> lines = lineService.findAll();
        Long lineId = lines.get(0).getId();

        lineService.update(lineId, "line2", "yellow");

        assertThat(lineService.findById(lineId))
                .extracting("name", "color")
                .containsExactly("line2", "yellow");

        lineService.delete(lineId);
    }
}
