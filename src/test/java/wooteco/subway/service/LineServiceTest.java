package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineCreateRequest;
import wooteco.subway.dto.LineCreateResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선을 저장할 수 있다.")
    void save() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600");

        // when
        Long savedId = lineService.save(request);

        // then
        LineCreateResponse lineCreateResponse = lineService.findById(savedId);
        assertThat(lineCreateResponse).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        LineCreateRequest request1 = new LineCreateRequest("신분당선", "bg-red-600");
        LineCreateRequest request2 = new LineCreateRequest("분당선", "bg-green-600");
        lineService.save(request1);
        lineService.save(request2);

        // when
        List<LineCreateResponse> lineCreateResponses = lineService.findAll();

        // then
        assertThat(lineCreateResponses).hasSize(2)
                .extracting("name", "color")
                .contains(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600")
                );
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600");
        final Long saveId = lineService.save(request);

        // when
        final LineCreateRequest updateRequest = new LineCreateRequest("다른분당선", "bg-red-600");
        Long updateId = lineService.updateByLine(saveId, updateRequest);

        // then
        final LineCreateResponse response = lineService.findById(updateId);
        assertThat(response).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("")
    void deleteById() {
        // given
        LineCreateRequest request = new LineCreateRequest("신분당선", "bg-red-600");
        final Long saveId = lineService.save(request);

        // when & then
        assertDoesNotThrow(() -> lineService.deleteById(saveId));
    }
}
