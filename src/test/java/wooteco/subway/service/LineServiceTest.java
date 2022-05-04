package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

class LineServiceTest {

    private final LineService lineService = new LineService();

    @Test
    @DisplayName("지하철 노선 추가, 조회, 삭제 테스트")
    void LineCRDTest() {
        lineService.save(new LineRequest("line1", "red", null, null, 0));
        lineService.save(new LineRequest("line2", "yellow", null, null, 0));
        lineService.save(new LineRequest("line3", "blue", null, null, 0));

        List<LineResponse> lines = lineService.findAll();

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
        lineService.save(new LineRequest("line1", "red", null, null, 0));

        assertThatThrownBy(() -> lineService.save(
                new LineRequest("line1", "yellow", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 노선 이름입니다.");

        List<LineResponse> lines = lineService.findAll();
        lineService.delete(lines.get(0).getId());
    }

    @Test
    @DisplayName("중복된 노선 색깔 입력 시 예외 발생 테스트")
    void validateDuplicationColorTest() {
        lineService.save(new LineRequest("line1", "red", null, null, 0));

        assertThatThrownBy(() -> lineService.save(new LineRequest("line2", "red", null, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 노선 색깔입니다.");

        List<LineResponse> lines = lineService.findAll();
        lineService.delete(lines.get(0).getId());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateTest() {
        LineResponse lineResponse = lineService.save(
                new LineRequest("line1", "red", null, null, 0));
        Long lineId = lineResponse.getId();

        lineService.update(lineId, new LineRequest(
                "line2", "yellow", null, null, 0));

        assertThat(lineService.findById(lineId))
                .extracting("name", "color")
                .containsExactly("line2", "yellow");

        lineService.delete(lineId);
    }

    @Test
    @DisplayName("없는 노선을 제거하면 예외가 발생한다.")
    void deleteNotExistLine() {
        assertThatThrownBy(() -> lineService.delete(0l))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 지하철 노선입니다.");
    }
}
