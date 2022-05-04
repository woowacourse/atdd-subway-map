package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600");
        LineResponse lineResponse = lineService.create(lineRequest);

        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor())
        );
    }

}
