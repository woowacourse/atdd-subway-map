package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Sql("/truncate.sql")
class LineServiceTest {

    private final LineService lineService;

    @Autowired
    public LineServiceTest(LineService lineService) {
        this.lineService = lineService;
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void 노선_저장() {
        String name = "2호선";
        String color = "bg-green-600";
        LineRequest lineRequest = new LineRequest(name, color);

        LineResponse lineResponse = lineService.save(lineRequest);

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(name),
                () -> assertThat(lineResponse.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("중복된 이름의 노선을 저장할 경우 예외를 발생시킨다.")
    @Test
    void 중복된_노선_저장_예외발생() {
        String name = "2호선";
        String color = "bg-green-600";
        LineRequest lineRequest = new LineRequest(name, color);

        lineService.save(lineRequest);

        assertThatThrownBy(() -> lineService.save(lineRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void 노선_조회() {
        String name = "2호선";
        String color = "bg-green-600";
        LineRequest lineRequest = new LineRequest(name, color);
        LineResponse lineResponse = lineService.save(lineRequest);

        LineResponse findResponse = lineService.findById(lineResponse.getId());

        assertAll(
                () -> assertThat(findResponse.getName()).isEqualTo(name),
                () -> assertThat(findResponse.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("존재하지 않는 노선을 조회할 경우 예외를 발생시킨다.")
    @Test
    void 존재하지_않는_노선_조회_예외발생() {
        assertThatThrownBy(() -> lineService.findById(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("다수의 노선을 조회한다.")
    @Test
    void 다수_노선_조회() {
        lineService.save(new LineRequest("2호선", "bg-green-600"));
        lineService.save(new LineRequest("수인분당선", "bg-yellow-600"));

        List<LineResponse> lines = lineService.findAll();

        assertThat(lines.size()).isEqualTo(2);
    }

    @DisplayName("존재하는 노선을 수정한다.")
    @Test
    void 노선_수정() {
        String name = "2호선";
        String color = "bg-green-600";
        LineResponse lineResponse = lineService.save(new LineRequest(name, color));

        String updateColor = "bg-blue-600";
        LineRequest updateRequest = new LineRequest(name, updateColor);
        LineResponse updatedResponse = lineService.update(lineResponse.getId(), updateRequest);

        assertAll(
                () -> assertThat(updatedResponse.getName()).isEqualTo(name),
                () -> assertThat(updatedResponse.getColor()).isEqualTo(updateColor)
        );
    }

    @DisplayName("존재하지 않는 노선을 수정하는 경우 새롭게 생성한다.")
    @Test
    void 존재하지_않는_노선_수정() {
        String name = "2호선";
        String updateColor = "bg-blue-600";
        LineRequest updateRequest = new LineRequest(name, updateColor);
        LineResponse lineResponse = lineService.update(0L, updateRequest);

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(name),
                () -> assertThat(lineResponse.getColor()).isEqualTo(updateColor)
        );
    }

    @DisplayName("중복된 이름을 가진 노선으로 수정할 경우 예외를 던진다.")
    @Test
    void 중복된_이름_노선_수정_예외발생() {
        lineService.save(new LineRequest("2호선", "bg-green-600"));
        LineResponse lineResponse = lineService.save(new LineRequest("3호선", "bg-orange-600"));

        String updateName = "2호선";
        String updateColor = "bg-green-600";
        LineRequest updateRequest = new LineRequest(updateName, updateColor);

        assertThatThrownBy(() -> lineService.update(lineResponse.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void 노선_삭제() {
        LineResponse lineResponse = lineService.save(new LineRequest("2호선", "bg-green-600"));

        lineService.deleteById(lineResponse.getId());

        assertThat(lineService.findAll().size()).isEqualTo(0);
    }
}
