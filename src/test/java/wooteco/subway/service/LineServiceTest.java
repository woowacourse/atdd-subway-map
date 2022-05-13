package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.repository.LineRepository;
import wooteco.subway.domain.repository.LineRepositoryImpl;
import wooteco.subway.domain.repository.SectionRepositoryImpl;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.utils.exception.DuplicatedException;
import wooteco.subway.utils.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class LineServiceTest {

    private static final long LINE_ID = 1L;
    private static final long NONE_LINE_ID = 5L;
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
                .isInstanceOf(DuplicatedException.class).hasMessage("[ERROR] 이미 존재하는 노선의 이름입니다.");
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        int 노선에_포함된_모든_역의_개수 = 3;
        List<LineResponse> lineResponses = lineService.getLines();
        assertAll(
                () -> assertThat(lineResponses).hasSize(1),
                () -> assertThat(lineResponses.get(0).getStations()).hasSize(노선에_포함된_모든_역의_개수)
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

    @DisplayName("존재하지 않는 아이디로 노선을 업데이트 하려고 하면 예외가 발생한다.")
    @Test
    void updateFailure() {
        assertThatThrownBy(
                () -> lineService.update(NONE_LINE_ID, new LineRequest("신분당선", "bg-yellow-600"))
        ).isExactlyInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 식별자에 해당하는 노선을 찾을수 없습니다.");
    }

    @DisplayName("노선을 제거 한다.")
    @Test
    void delete() {
        lineService.deleteById(LINE_ID);

        assertThat(lineRepository.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 아이디로 노선을 제거하려고 하면 예외가 발생한다.")
    @Test
    void deleteFailure() {
        assertThatThrownBy(
                () -> lineService.deleteById(NONE_LINE_ID)
        ).isExactlyInstanceOf(NotFoundException.class).hasMessage("[ERROR] 식별자에 해당하는 노선을 찾을수 없습니다.");
    }

}
