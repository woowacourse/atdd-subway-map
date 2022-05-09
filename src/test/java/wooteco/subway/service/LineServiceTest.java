package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
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

    @Autowired
    private DataSource dataSource;

    private LineService lineService;
    private LineRepository lineRepository;
    private SectionService sectionService;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;
    private Station station1;
    private Station station2;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepositoryImpl(dataSource);
        sectionRepository = new SectionRepositoryImpl(dataSource);
        lineService = new LineService(lineRepository, sectionService, sectionRepository);
        stationRepository = new StationRepositoryImpl(dataSource);
        station1 = stationRepository.save(new Station("홍대입구역"));
        station2 = stationRepository.save(new Station("신촌역"));

    }

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        LineRequest lineRequest = new LineRequest("분당선", "bg-red-600");
        Line line = lineService.create(lineRequest);

        assertAll(
                () -> assertThat(line.getId()).isNotNull(),
                () -> assertThat(line.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(line.getColor()).isEqualTo(lineRequest.getColor())
        );
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
        Line line1 = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line line2 = lineRepository.save(new Line("신분당선", "bg-yellow-600"));
        Station station3 = stationRepository.save(new Station("잠실역"));
        Station station4 = stationRepository.save(new Station("선릉역"));
        sectionRepository.save(new Section(line1.getId(), station1, station2, 10));
        sectionRepository.save(new Section(line2.getId(), station3, station4, 10));
        List<LineResponse> lineResponses = lineService.getLines();
        assertAll(
                () -> assertThat(lineResponses).hasSize(2),
                () -> assertThat(lineResponses.get(0).getStations()).hasSize(2),
                () -> assertThat(lineResponses.get(1).getStations()).hasSize(2)
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        Line line = lineRepository.save(new Line("분당선", "bg-red-600"));
        LineResponse lineResponse = lineService.getLine(line.getId());

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600")
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
        Line line = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineService.delete(line.getId());

        assertThat(lineRepository.findAll()).isEmpty();
    }

}
