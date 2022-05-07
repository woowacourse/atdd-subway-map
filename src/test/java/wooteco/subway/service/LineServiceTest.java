package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}
