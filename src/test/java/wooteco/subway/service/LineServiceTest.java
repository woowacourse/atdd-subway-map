package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.exception.NotFoundLineException;

@Transactional
@JdbcTest
class LineServiceTest {

    StationResponse createdStation1;
    StationResponse createdStation2;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LineService lineService;
    private StationService stationService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new LineDao(jdbcTemplate), new SectionDao(jdbcTemplate));
        stationService = new StationService(new StationDao(jdbcTemplate));

        createdStation1 = stationService.createStation(new StationRequest("선릉역"));
        createdStation2 = stationService.createStation(new StationRequest("잠실역"));
    }

    // TODO: stations 도 함께 LineResponse에 포함해야함
    @DisplayName("이름, 색상, 상행선, 하행선, 길이를 전달받아 새로운 노선을 등록한다.")
    @Test
    void createLine() {
        // given
        String name = "2호선";
        String color = "bg-green-600";
        Long upStationId = createdStation1.getId();
        Long downStationId = createdStation2.getId();
        Integer distance = 10;

        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        // when
        LineResponse actual = lineService.createLine(lineRequest);

        // then
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(name),
                () -> assertThat(actual.getColor()).isEqualTo(color)
        );
    }

    @DisplayName("중복된 이름의 노선을 등록할 경우 예외를 발생한다.")
    @Test
    void createLine_throwsExceptionWithDuplicateName() {
        // given
        String name = "2호선";
        String color = "bg-green-600";
        Long upStationId = createdStation1.getId();
        Long downStationId = createdStation2.getId();
        Integer distance = 10;

        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        lineService.createLine(lineRequest);

        // when & then
        assertThatThrownBy(() -> lineService.createLine(lineRequest))
                .isInstanceOf(DuplicateNameException.class);
    }

    @DisplayName("등록된 모든 노선을 반환한다.")
    @Test
    void getAllLines() {
        // given
        Line line1 = new Line("1호선", "bg-blue-600");
        Line line2 = new Line("2호선", "bg-green-600");
        LineRequest lineRequest1 = new LineRequest(line1.getName(), line1.getColor(), createdStation1.getId(),
                createdStation2.getId(), 10);
        LineRequest lineRequest2 = new LineRequest(line2.getName(), line2.getColor(), createdStation1.getId(),
                createdStation2.getId(), 10);

        lineService.createLine(lineRequest1);
        lineService.createLine(lineRequest2);

        // when
        List<Line> actual = lineService.getAllLines().stream()
                .map(lineResponse -> new Line(lineResponse.getName(), lineResponse.getColor()))
                .collect(Collectors.toList());

        List<Line> expected = List.of(line1, line2);

        // then
        assertThat(actual).containsAll(expected);
    }

    @DisplayName("노선 ID로 개별 노선을 찾아 반환한다.")
    @Test
    void getLineById() {
        // given
        Line line = new Line("1호선", "bg-blue-600");
        LineRequest lineRequest = new LineRequest(line.getName(), line.getColor(), createdStation1.getId(),
                createdStation2.getId(), 10);

        LineResponse createdLine = lineService.createLine(lineRequest);

        // when
        LineResponse actual = lineService.getLineById(createdLine.getId());

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(createdLine.getId()),
                () -> assertThat(actual.getName()).isEqualTo(createdLine.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(createdLine.getColor())
        );
    }

    @DisplayName("노선 ID로 노선을 업데이트 한다.")
    @Test
    void updateLine() {
        // given
        Line line = new Line("1호선", "bg-blue-600");
        LineRequest lineRequest = new LineRequest(line.getName(), line.getColor(), createdStation1.getId(),
                createdStation2.getId(), 10);
        LineResponse createdLine = lineService.createLine(lineRequest);

        // when
        Line newLine = new Line("2호선", "bg-red-600");
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(newLine.getName(), newLine.getColor());
        lineService.update(createdLine.getId(), lineUpdateRequest);

        // then
        LineResponse actual = lineService.getLineById(createdLine.getId());
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(lineUpdateRequest.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(lineUpdateRequest.getColor())
        );
    }

    @DisplayName("수정하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void update_throwsExceptionIfLineIdIsNotExisting() {
        // given
        Line newLine = new Line("2호선", "bg-red-600");
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest(newLine.getName(), newLine.getColor());

        // when & then
        assertThatThrownBy(() -> lineService.update(10L, lineUpdateRequest))
                .isInstanceOf(NotFoundLineException.class);
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void delete() {
        // given
        Line line = new Line("1호선", "bg-blue-600");
        LineRequest lineRequest = new LineRequest(line.getName(), line.getColor(), createdStation1.getId(),
                createdStation2.getId(), 10);
        LineResponse createdLine = lineService.createLine(lineRequest);

        // when
        lineService.delete(createdLine.getId());

        // then
        boolean isNotExistLine = lineService.getAllLines()
                .stream()
                .filter(lineResponse -> lineResponse.getId().equals(createdLine.getId()))
                .findAny()
                .isEmpty();

        assertThat(isNotExistLine).isTrue();
    }

    @DisplayName("삭제하려는 노선 ID가 존재하지 않을 경우 예외를 발생한다.")
    @Test
    void delete_throwsExceptionIfLineIdIsNotExisting() {
        assertThatThrownBy(() -> lineService.delete(1L))
                .isInstanceOf(NotFoundLineException.class);
    }
}
