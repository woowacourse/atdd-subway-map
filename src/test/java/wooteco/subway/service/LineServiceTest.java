package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.*;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.utils.exception.DuplicatedException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class LineServiceTest {

    private static final long LINE_ID = 1L;
    @Autowired
    private DataSource dataSource;

    private LineService lineService;
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepositoryImpl(dataSource);
        lineService = new LineService(lineRepository,
                new SectionRepositoryImpl(dataSource));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600");
        Line line = lineService.create(lineRequest);

        assertThat(line.getId()).isNotNull();
        assertThat(line).extracting(Line::getName, Line::getColor)
                .containsExactly(lineRequest.getName(), lineRequest.getColor());
    }

    @DisplayName("노선 생성시 이름이 존재할 경우 예외 발생")
    @Test
    void createDuplicateName() {
        lineRepository.save(new Line("분당선", "bg-red-600"));
        assertThatThrownBy(() -> lineService.create(new LineRequest("분당선", "bg-red-600")))
                .isInstanceOf(DuplicatedException.class);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {

        List<LineResponse> lineResponses = lineService.getLines();
        assertAll(
                () -> assertThat(lineResponses).hasSize(1),
                () -> assertThat(lineResponses.get(0).getStations()).hasSize(3)
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        Line line = lineRepository.findById(LINE_ID).get();
        LineResponse lineResponse = lineService.getLine(line.getId());

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("2호선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("노선을 업데이트 한다.")
    @Test
    void update() {
        Line line = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineService.update(line.getId(), new LineRequest("신분당선", "bg-yellow-600"));

        Line findUpdateLine = lineRepository.findById(line.getId()).get();
        assertAll(
                () -> assertThat(findUpdateLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(findUpdateLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }


    @DisplayName("노선을 제거 한다.")
    @Test
    void delete() {
        lineService.delete(LINE_ID);

        assertThat(lineRepository.findAll()).isEmpty();
    }

}
