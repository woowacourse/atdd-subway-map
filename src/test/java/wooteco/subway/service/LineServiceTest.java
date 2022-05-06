package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Test
    @DisplayName("노선을 저장할 수 있다.")
    void save() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");

        // when
        Long savedId = lineService.save(request);

        // then
        LineResponse response = lineService.findById(savedId);
        assertThat(response).extracting("name", "color")
                .contains("신분당선", "bg-red-600");
    }

    @Test
    @DisplayName("전체 노선을 조회할 수 있다.")
    void findAll() {
        // given
        LineRequest request1 = new LineRequest("신분당선", "bg-red-600");
        LineRequest request2 = new LineRequest("분당선", "bg-green-600");
        lineService.save(request1);
        lineService.save(request2);

        // when
        List<LineResponse> responses = lineService.findAll();

        // then
        assertThat(responses).hasSize(2)
                .extracting("name", "color")
                .containsExactlyInAnyOrder(
                        tuple("신분당선", "bg-red-600"),
                        tuple("분당선", "bg-green-600")
                );
    }

    @Test
    @DisplayName("기존 노선의 이름과 색상을 변경할 수 있다.")
    void updateById() {
        // given
        final LineRequest request = new LineRequest("신분당선", "bg-red-600");
        final Long savedId = lineService.save(request);

        // when
        final LineRequest updateRequest = new LineRequest("다른분당선", "bg-red-600");
        Long updateId = lineService.updateByLine(savedId, updateRequest);

        // then
        final LineResponse response = lineService.findById(updateId);
        assertThat(response).extracting("name", "color")
                .contains("다른분당선", "bg-red-600");
    }

    @Test
    @DisplayName("노선을 삭제할 수 있다.")
    void deleteById() {
        // given
        LineRequest request = new LineRequest("신분당선", "bg-red-600");
        final Long savedId = lineService.save(request);

        // when & then
        assertDoesNotThrow(() -> lineService.deleteById(savedId));
    }
    
    @DisplayName("존재하지 않는 id로 조회하면 예외가 발생한다.")
    @Test
    public void findByNotExistId() {
        // given
        final LineRequest request = new LineRequest("신분당선", "bg-red-600");
        final Long savedId = lineService.save(request);

        // when & then
        Assertions.assertThatThrownBy(() -> lineService.findById(savedId + 1))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
