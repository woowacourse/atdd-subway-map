package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DuplicateLineException;

@SpringBootTest
@Transactional
class LineServiceTest {

    private final LineService lineService;

    @Autowired
    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @Test
    @DisplayName("지하철 노선 추가 테스트")
    void LineCreateTest() {
        LineResponse lineResponse = lineService.save(
            new LineRequest("신분당선", "red", null, null, 0));

        assertThat(lineService.findById(lineResponse.getId()))
            .extracting("name", "color")
            .containsExactly("신분당선", "red");
    }

    @Test
    @DisplayName("지하철 노선 단건 조회 테스트")
    void LineReadOneTest() {
        LineResponse lineResponse = lineService.save(
            new LineRequest("신분당선", "red", null, null, 0));

        LineResponse result = lineService.findById(lineResponse.getId());

        assertThat(result)
            .extracting("name", "color")
            .containsExactly("신분당선", "red");
    }

    @Test
    @DisplayName("지하철 노선 삭제 테스트")
    void LineDeleteTest() {
        Long deleteId = lineService.save(
            new LineRequest("1호선", "blue", null, null, 0)).getId();

        lineService.delete(deleteId);

        assertThatThrownBy(() -> lineService.findById(deleteId))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("지하철 노선 전체 조회 테스트")
    void findAllTest() {
        lineService.save(new LineRequest("line1", "green", null, null, 0));
        lineService.save(new LineRequest("line2", "yellow", null, null, 0));
        lineService.save(new LineRequest("line3", "blue", null, null, 0));

        List<LineResponse> lines = lineService.findAll();

        assertThat(lines).hasSize(3)
            .extracting("name", "color")
            .containsExactly(
                tuple("line1", "green"),
                tuple("line2", "yellow"),
                tuple("line3", "blue"));
    }

    @Test
    @DisplayName("중복된 노선 이름 입력 시 예외 발생 테스트")
    void validateDuplicationNameTest() {
        lineService.save(new LineRequest("line1", "red", null, null, 0));

        assertThatThrownBy(() -> lineService.save(
            new LineRequest("line1", "yellow", null, null, 0)))
            .isInstanceOf(DuplicateLineException.class)
            .hasMessageContaining("이미 존재하는 노선 이름입니다.");
    }

    @Test
    @DisplayName("중복된 노선 색깔 입력 시 예외 발생 테스트")
    void validateDuplicationColorTest() {
        lineService.save(new LineRequest("line1", "red", null, null, 0));

        assertThatThrownBy(() -> lineService.save(new LineRequest("line2", "red", null, null, 0)))
            .isInstanceOf(DuplicateLineException.class)
            .hasMessageContaining("이미 존재하는 노선 색깔입니다.");
    }

    @Test
    @DisplayName("노선 업데이트 테스트")
    void updateTest() {
        LineResponse lineResponse = lineService.save(
            new LineRequest("line1", "red", null, null, 0));
        Long lineId = lineResponse.getId();

        lineService.update(lineId, new LineRequest(
            "line2", "yellow", null, null, 0));

        assertThat(lineService.findById(lineId))
            .extracting("name", "color")
            .containsExactly("line2", "yellow");
    }

    @Test
    @DisplayName("이미 있는 노선 이름으로 업데이트 시 예외 발생 테스트")
    void updateDuplicateNameExceptionTest() {
        LineResponse lineResponse1 = lineService.save(
            new LineRequest("1호선", "red", null, null, 0));
        LineResponse lineResponse2 = lineService.save(
            new LineRequest("2호선", "blue", null, null, 0));

        assertThatThrownBy(() ->
            lineService.update(lineResponse1.getId(),
                new LineRequest("2호선", "yellow", null, null, 0)))
            .isInstanceOf(DuplicateLineException.class)
            .hasMessageContaining("이미 존재하는 노선 이름으로 업데이트할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 있는 노선 색깔로 업데이트 시 예외 발생 테스트")
    void updateDuplicateColorExceptionTest() {
        LineResponse lineResponse1 = lineService.save(
            new LineRequest("1호선", "red", null, null, 0));
        LineResponse lineResponse2 = lineService.save(
            new LineRequest("2호선", "blue", null, null, 0));

        assertThatThrownBy(() ->
            lineService.update(lineResponse1.getId(),
                new LineRequest("1호선", "blue", null, null, 0)))
            .isInstanceOf(DuplicateLineException.class)
            .hasMessageContaining("이미 존재하는 노선 색깔로 업데이트할 수 없습니다.");
    }
}
