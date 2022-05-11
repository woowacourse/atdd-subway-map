package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequestDto;
import wooteco.subway.dto.request.SectionRequestDto;
import wooteco.subway.dto.request.StationRequestDto;
import wooteco.subway.exception.CanNotDeleteException;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.mockDao.MockLineDao;
import wooteco.subway.mockDao.MockSectionDao;
import wooteco.subway.mockDao.MockStationDao;

class LineServiceTest {

    private final MockStationDao mockStationDao = new MockStationDao();
    private final MockLineDao mockLineDao = new MockLineDao();
    private final MockSectionDao mockSectionDao = new MockSectionDao();
    private final StationService stationService = new StationService(mockStationDao, mockSectionDao);

    private final LineService lineService = new LineService(mockLineDao, mockSectionDao, stationService);

    private Station station_SDB;
    private Station station_GN;
    private Station station_YS;
    private Station station_SL;
    private Station station_JS;
    private LineRequestDto lineRequestDto_2H;
    private LineRequestDto lineRequestDto_SBD;
    private LineRequestDto lineRequestDto_BD;
    private SectionRequestDto sectionRequestDto_SDB_GN;
    private SectionRequestDto sectionRequestDto_GN_YS;
    private SectionRequestDto sectionRequestDto_SL_JS;

    @BeforeEach
    void setUp() {
        mockStationDao.removeAll();
        mockLineDao.removeAll();
        mockSectionDao.removeAll();

        station_SDB = stationService.register(new StationRequestDto("신대방역"));
        station_GN = stationService.register(new StationRequestDto("강남역"));
        station_YS = stationService.register(new StationRequestDto("역삼역"));
        station_SL = stationService.register(new StationRequestDto("선릉역"));
        station_JS = stationService.register(new StationRequestDto("잠실역"));
        lineRequestDto_2H = new LineRequestDto(
                "2호선", "bg-green-600", station_GN.getId(), station_JS.getId(), 100
        );
        lineRequestDto_SBD = new LineRequestDto(
                "신분당선", "bg-red-600", station_GN.getId(), station_JS.getId(), 50
        );
        lineRequestDto_BD = new LineRequestDto(
                "분당선", "bg-yellow-600", station_GN.getId(), station_JS.getId(), 25
        );
        sectionRequestDto_SDB_GN = new SectionRequestDto(station_SDB.getId(), station_GN.getId(), 10);
        sectionRequestDto_GN_YS = new SectionRequestDto(station_GN.getId(), station_YS.getId(), 20);
        sectionRequestDto_SL_JS = new SectionRequestDto(station_SL.getId(), station_JS.getId(), 30);
    }

    @DisplayName("노선 이름과 색깔을 입력받아서 해당 이름과 색깔을 가진 노선을 등록한다.")
    @Test
    void registerLine() {
        Line createdLine = lineService.registerLine(lineRequestDto_2H);

        assertAll(
                () -> assertThat(createdLine.getName()).isEqualTo("2호선"),
                () -> assertThat(createdLine.getColor()).isEqualTo("bg-green-600"),
                () -> assertThat(createdLine.getStations().size()).isEqualTo(2)
        );
    }

    @DisplayName("이미 존재하는 노선이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerLineDuplicateName() {
        lineService.registerLine(lineRequestDto_2H);

        assertThatThrownBy(() -> lineService.registerLine(lineRequestDto_2H))
                .isInstanceOf(DuplicateLineNameException.class)
                .hasMessage("[ERROR] 이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void searchLineById() {
        Line savedLine = lineService.registerLine(lineRequestDto_2H);

        Line searchedLine = lineService.searchLineById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo(savedLine.getName()),
                () -> assertThat(searchedLine.getColor()).isEqualTo(savedLine.getColor())
        );
    }

    @DisplayName("등록된 모든 노선 리스트를 조회한다.")
    @Test
    void searchAllLines() {
        lineService.registerLine(lineRequestDto_2H);
        lineService.registerLine(lineRequestDto_SBD);
        lineService.registerLine(lineRequestDto_BD);

        List<Line> lines = lineService.searchAllLines();
        List<String> names = lines.stream()
                .map(Line::getName)
                .collect(Collectors.toList());
        List<String> colors = lines.stream()
                .map(Line::getColor)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names).isEqualTo(List.of("2호선", "신분당선", "분당선")),
                () -> assertThat(colors).isEqualTo(List.of("bg-green-600", "bg-red-600", "bg-yellow-600"))
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        Line savedLine = lineService.registerLine(lineRequestDto_2H);

        lineService.modifyLine(savedLine.getId(), lineRequestDto_SBD);
        Line searchedLine = lineService.searchLineById(savedLine.getId());

        assertAll(
                () -> assertThat(searchedLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(searchedLine.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("id 로 노선을 삭제한다.")
    @Test
    void removeLine() {
        lineService.registerLine(lineRequestDto_2H);
        Line line = lineService.registerLine(lineRequestDto_SBD);

        lineService.removeLine(line.getId());

        assertThat(lineService.searchAllLines().size()).isEqualTo(1);
    }

    @DisplayName("구간이 노선 위에 존재 할 때 구간을 등록한다.")
    @Test
    void registerSectionWhenOnTheLine() {
        Line line = lineService.registerLine(lineRequestDto_2H);

        lineService.registerSection(line.getId(), sectionRequestDto_GN_YS);

        List<Section> sections = lineService.searchSectionsByLineId(line.getId());
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(sections.stream().mapToInt(Section::getDistance).sum()).isEqualTo(100)
        );
    }

    @DisplayName("구간이 노선 밖에 존재 할 때 구간을 등록한다.")
    @Test
    void registerSectionWhenOutOfTheLine() {
        Line line = lineService.registerLine(lineRequestDto_2H);

        lineService.registerSection(line.getId(), sectionRequestDto_SDB_GN);

        List<Section> sections = lineService.searchSectionsByLineId(line.getId());
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(sections.stream().mapToInt(Section::getDistance).sum()).isEqualTo(110)
        );
    }

    @DisplayName("노선에 존재하는 모든 구간을 조회한다.")
    @Test
    void searchSectionsByLineId() {
        Line line = lineService.registerLine(lineRequestDto_2H);
        lineService.registerSection(line.getId(), sectionRequestDto_SDB_GN);
        lineService.registerSection(line.getId(), sectionRequestDto_SL_JS);

        List<Section> sections = lineService.searchSectionsByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(3);
    }

    @DisplayName("노선 id 와 역 id 로 상행 종점 구간을 삭제한다.")
    @Test
    void removeSectionTerminus() {
        Line line = lineService.registerLine(lineRequestDto_2H);
        lineService.registerSection(line.getId(), sectionRequestDto_SL_JS);

        lineService.removeSection(line.getId(), station_JS.getId());

        List<Section> sections = lineService.searchSectionsByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(1);
    }

    @DisplayName("노선 id 와 역 id 로 노선의 중간 구간을 삭제한다.")
    @Test
    void removeSectionOnTheLine() {
        Line line = lineService.registerLine(lineRequestDto_2H);
        lineService.registerSection(line.getId(), sectionRequestDto_GN_YS);
        lineService.registerSection(line.getId(), sectionRequestDto_SL_JS);

        lineService.removeSection(line.getId(), station_SL.getId());

        List<Section> sections = lineService.searchSectionsByLineId(line.getId());
        assertThat(sections.size()).isEqualTo(2);
    }

    @DisplayName("노선에 존재 하지 않는 구간을 삭제하려고할 시 예외가 발생한다.")
    @Test
    void removeSectionOutOfLine() {
        Line line = lineService.registerLine(lineRequestDto_2H);
        lineService.registerSection(line.getId(), sectionRequestDto_GN_YS);
        lineService.registerSection(line.getId(), sectionRequestDto_SL_JS);

        assertThatThrownBy(() -> lineService.removeSection(line.getId(), station_SDB.getId()))
                .isInstanceOf(CanNotDeleteException.class)
                .hasMessage("[ERROR] 삭제 할 수 없습니다.");
    }

    @DisplayName("구간이 하나만 존재하는 노선에서 구간을 삭제하려고할 시 예외가 발생한다.")
    @Test
    void removeSectionWhenSingleSection() {
        Line line = lineService.registerLine(lineRequestDto_2H);

        assertThatThrownBy(() -> lineService.removeSection(line.getId(), station_GN.getId()))
                .isInstanceOf(CanNotDeleteException.class)
                .hasMessage("[ERROR] 삭제 할 수 없습니다.");
    }
}
