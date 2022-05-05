package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 노선을 저장한다.")
    @Test
    void create() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");

        LineResponse lineResponse = lineService.create(lineRequest);

        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getName()).isEqualTo(lineResponse.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineResponse.getColor());
    }

    @DisplayName("이미 저장된 노선과 중복된 이름의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        LineRequest duplicateRequest = new LineRequest("2호선", "빨간색");

        lineService.create(lineRequest);

        assertThatThrownBy(() -> lineService.create(duplicateRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }

    @DisplayName("이미 저장된 노선과 중복된 이름의 노선을 저장하려 하면 예외가 발생한다.")
    @Test
    void createDuplicateColor() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        LineRequest duplicateRequest = new LineRequest("성수지선", "초록색");

        lineService.create(lineRequest);

        assertThatThrownBy(() -> lineService.create(duplicateRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessage("이미 존재하는 노선입니다.");
    }

    @DisplayName("저장된 노선을 모두 조회한다.")
    @Test
    void showAll() {
        LineRequest request1 = new LineRequest("1호선", "군청색");
        LineRequest request2 = new LineRequest("2호선", "초록색");
        lineService.create(request1);
        lineService.create(request2);

        List<LineResponse> lineResponses = lineService.showAll();

        assertThat(lineResponses.size()).isEqualTo(2);
    }

    @DisplayName("지정한 id에 해당하는 노선을 조회한다.")
    @Test
    void show() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();

        LineResponse actual = lineService.show(id);
        LineResponse expected = new LineResponse(id, "2호선", "초록색");

        assertEquals(expected, actual);
    }

    @DisplayName("지정한 id에 해당하는 노선이 없으면 예외가 발생한다.")
    @Test
    void showNotExist() {
        assertThatThrownBy(() -> lineService.show(Long.MAX_VALUE))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("지정한 id에 해당하는 노선 정보를 수정한다.")
    @Test
    void update() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();

        LineRequest updateRequest = new LineRequest("1호선", "군청색");
        lineService.update(id, updateRequest);
        LineResponse expected = new LineResponse(id, "1호선", "군청색");
        LineResponse actual = lineService.show(id);

        assertEquals(expected, actual);
    }

    @DisplayName("수정하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void updateNotExist() {
        LineRequest updateRequest = new LineRequest("2호선", "초록색");

        assertThatThrownBy(() -> lineService.update(Long.MAX_VALUE, updateRequest))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    @DisplayName("지정한 id에 해당하는 노선을 삭제한다.")
    @Test
    void delete() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        Long id = lineService.create(lineRequest).getId();

        lineService.delete(id);
        List<LineResponse> lineResponses = lineService.showAll();

        assertThat(lineResponses).isEmpty();
    }

    @DisplayName("삭제하려는 노선이 없으면 예외가 발생한다.")
    @Test
    void deleteNotExist() {
        assertThatThrownBy(() -> lineService.delete(Long.MAX_VALUE))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("존재하지 않는 노선입니다.");
    }

    private void assertEquals(LineResponse expected, LineResponse actual) {
        assertThat(expected.getId()).isEqualTo(actual.getId());
        assertThat(expected.getName()).isEqualTo(actual.getName());
        assertThat(expected.getColor()).isEqualTo(actual.getColor());
    }

}
