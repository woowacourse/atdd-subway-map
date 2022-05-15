package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.LineService;
import wooteco.subway.application.SectionService;
import wooteco.subway.application.StationService;
import wooteco.subway.application.UpwardSorter;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.AddSectionRequest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
@Transactional
@TestConstructor(autowireMode = AutowireMode.ALL)
public class LineDaoTest {

    private final LineDao lineDao;
    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;

    public LineDaoTest(LineDao lineDao, LineService lineService,
                       StationService stationService, SectionService sectionService) {
        this.lineDao = lineDao;
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    private Line line1;
    private Line line2;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        station1 = stationService.save(new StationRequest("강남역"));
        station2 = stationService.save(new StationRequest("역삼역"));
        station3 = stationService.save(new StationRequest("선릉역"));
        station4 = stationService.save(new StationRequest("잠실역"));

        line1 = lineService.save(new LineRequest("신분당선", "bg-red-600",
            station1.getId(), station2.getId(), 10));
        line2 = lineService.save(new LineRequest("1호선", "bg-blue-600",
            station1.getId(), station2.getId(), 10));

        sectionService.addSection(line1.getId(),
            new AddSectionRequest(station2.getId(), station3.getId(), 5));
        sectionService.addSection(line2.getId(),
            new AddSectionRequest(station1.getId(), station4.getId(), 4));
    }

    @Test
    void getById() {
        LineResponse lineResponse = lineDao.queryById(line1.getId(), new UpwardSorter())
            .orElseThrow();

        assertThat(lineResponse.getId()).isEqualTo(line1.getId());
        assertThat(lineResponse.getName()).isEqualTo(line1.getName());
        assertThat(lineResponse.getColor()).isEqualTo(line1.getColor());
        assertThat(findStationIds(lineResponse))
            .containsExactly(station1.getId(), station2.getId(), station3.getId());
    }

    @Test
    void getAll() {
        List<LineResponse> lineResponses = lineDao.queryAll(new UpwardSorter());

        assertThat(lineResponses)
            .hasSize(2)
            .extracting(LineResponse::getId, LineResponse::getName, LineResponse::getColor)
            .containsExactlyInAnyOrder(
                tuple(line1.getId(), line1.getName(), line1.getColor()),
                tuple(line2.getId(), line2.getName(), line2.getColor())
            );

        assertThat(lineResponses)
            .extracting(this::findStationIds)
            .containsExactlyInAnyOrder(
                List.of(station1.getId(), station2.getId(), station3.getId()),
                List.of(station1.getId(), station4.getId(), station2.getId())
            );
    }

    private List<Long> findStationIds(LineResponse lineResponse) {
        return lineResponse.getStations().stream().map(StationResponse::getId)
            .collect(Collectors.toList());
    }
}


