package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.DataNotFoundException;

@Sql(scripts = {"classpath:setupSchema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoFakeLineServiceTest {

    @Autowired
    private LineService lineService;

    @DisplayName("노선, 구간 정보를 저장한다.")
    @Test
    void save() {
        LineRequest line = new LineRequest("4호선", "green", 1L, 2L, 10);
        LineResponse newLine = lineService.createLine(line);

        assertThat(line.getName()).isEqualTo(newLine.getName());
        assertThat(line.getColor()).isEqualTo(newLine.getColor());

        assertThat(line.getUpStationId()).isEqualTo(newLine.getStations().get(0).getId());
        assertThat(line.getDownStationId()).isEqualTo(newLine.getStations().get(1).getId());
    }

    @DisplayName("노선, 구간 정보를 저장할때, 노선과 구간 정보에 존재하지 않는 지하철역 정보가 있으면 예외를 발생한다.")
    @Test
    void save_no_data_exception() {
        LineRequest line = new LineRequest("4호선", "green", 1L, 7L, 10);

        assertThatThrownBy(() -> lineService.createLine(line))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("존재하지 않는 역입니다.");
    }
}
