package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.service.StationService;

@DisplayName("Line Service")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class LineServiceTest {

    private final LineService lineService;
    private final StationService stationService;
    private final SectionService sectionService;
    private final SectionRepository sectionRepository;

    public LineServiceTest(LineService lineService,
        StationService stationService, SectionService sectionService,
        SectionRepository sectionRepository) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
    }

    @BeforeEach
    void setUp() {

    }

    @DisplayName("노선과 노선의 초기 구간이 생성된다.")
    @Test
    void createLine() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);

        // when
        Line line = lineService.createLine(lineRequest);

        // then
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo(lineRequest.getName());
        assertThat(line.getColor()).isEqualTo(lineRequest.getColor());

        List<Section> sections = sectionService.findAllByLineId(line.getId());
        assertThat(sections).hasSize(1);

        Section section = sections.get(0);
        assertThat(section.getId()).isEqualTo(1L);
        assertThat(section.getLineId()).isEqualTo(line.getId());
        assertThat(section.getUpStation())
            .isEqualTo(stationService.findById(lineRequest.getUpStationId()));
        assertThat(section.getDownStation())
            .isEqualTo(stationService.findById(lineRequest.getDownStationId()));
        assertThat(section.getDistance()).isEqualTo(lineRequest.getDistance());
    }

    @DisplayName("중복된 이름을 갖는 노선을 생성하면, 예외가 발생한다.")
    @Test
    void createDuplicateLineException() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);

        // when, then
        lineService.createLine(lineRequest);

        assertThatThrownBy(() -> lineService.createLine(lineRequest))
            .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        lineService.createLine(lineRequest);

        // when
        Line line = lineService.findLine(1L);

        // then
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("없는 노선을 조회하면, 예외를 던진다.")
    @Test
    void showNotExistLineException() {
        assertThatThrownBy(() -> lineService.findLine(1L))
            .isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void showLines() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        stationService.createStation(new StationRequest("C"));

        LineRequest lineTwoRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        LineRequest lineFourRequest = new LineRequest("4호선", "bg-blue-600", 2L, 3L, 10);

        Line lineTwo = lineService.createLine(lineTwoRequest);
        Line lineFour = lineService.createLine(lineFourRequest);

        // when
        List<Line> lines = lineService.findLines();

        // then
        Line firstLine = lines.get(0);
        assertThat(firstLine.getId()).isEqualTo(lineTwo.getId());
        assertThat(firstLine.getName()).isEqualTo(lineTwo.getName());
        assertThat(firstLine.getColor()).isEqualTo(lineTwo.getColor());

        Line secondLine = lines.get(lines.size() - 1);
        assertThat(secondLine.getId()).isEqualTo(lineFour.getId());
        assertThat(secondLine.getName()).isEqualTo(lineFour.getName());
        assertThat(secondLine.getColor()).isEqualTo(lineFour.getColor());
    }

    @DisplayName("조회되는 노선 목록이 없으면, 예외가 발생한다.")
    @Test
    void showNotExistLinesException() {
        assertThatThrownBy(lineService::findLines).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 100);
        Line originLine = lineService.createLine(lineRequest);
        LineRequest updateRequest = new LineRequest("3호선", "bg-orange-600");

        // when
        lineService.updateLine(originLine.getId(), updateRequest);

        Line updatedLine = lineService.findLine(1L);

        // then
        assertThat(updatedLine.getName()).isEqualTo(updateRequest.getName());
        assertThat(updatedLine.getColor()).isEqualTo(updateRequest.getColor());
    }

    @DisplayName("없는 노선을 수정하면, 예외가 발생한다.")
    @Test
    void updateException() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600");

        // when, then
        assertThatThrownBy(() -> {
            lineService.updateLine(1L, lineRequest);
        }).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        Line line = lineService.createLine(lineRequest);

        // when, then
        assertThatCode(() -> lineService.deleteLine(line.getId())).doesNotThrowAnyException();
        assertThatThrownBy(lineService::findLines).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("없는 노선을 삭제하면, 예외가 발생한다.")
    @Test
    void deleteException() {
        assertThatThrownBy(() -> lineService.deleteLine(1L))
            .isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선에 상행 종점역을 추가한다. (B-C에 A-B 구간 추가시, A-B-C)")
    @Test
    void addUpTerminalSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 2L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when
        lineService.addSection(lineId, new LineRequest(1L, 2L, 50));

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(1L, lineId, stationB, stationC, 100),
            new Section(2L, lineId, stationA, stationB, 50)
        );
    }

    @DisplayName("노선에 하행 종점역을 추가한다. (A-B에 B-C 구간 추가시, A-B-C)")
    @Test
    void addDownTerminalSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when
        lineService.addSection(lineId, new LineRequest(2L, 3L, 50));

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(1L, lineId, stationA, stationB, 100),
            new Section(2L, lineId, stationB, stationC, 50)
        );
    }

    @DisplayName("노선 중간에 역을 추가한다. (A-C에 A-B 구간 추가시, A-B-C)")
    @Test
    void addSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when
        lineService.addSection(lineId, new LineRequest(1L, 2L, 50));

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(2L, lineId, stationB, stationC, 50),
            new Section(3L, lineId, stationA, stationB, 50)
        );
    }

    @DisplayName("노선 중간에 역을 추가한다. (A-C에 B-C 구간 추가시, A-B-C)")
    @Test
    void addSection2() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when
        lineService.addSection(lineId, new LineRequest(2L, 3L, 50));

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(3L, lineId, stationB, stationC, 50),
            new Section(2L, lineId, stationA, stationB, 50)
        );
    }

    @DisplayName("중복되는 구간을 추가하면, 예외를 던진다. (A-B에 A-B 구간 추가시)")
    @Test
    void duplicatedSectionException() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when then
        assertThatThrownBy(() -> {
            lineService.addSection(lineId, new LineRequest(1L, 3L, 100));
        }).isInstanceOf(DuplicatedSectionException.class);
    }

    @DisplayName("추가하는 구간의 역이 둘 다 노선에 포함되지 않는다면, 예외를 던진다. (A-B에 X-Y 구간 추가시)")
    @Test
    void notContainStationsException() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when then
        assertThatThrownBy(() -> {
            lineService.addSection(lineId, new LineRequest(3L, 4L, 100));
        }).isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("노선 중간에 역을 추가할시 기존 역사이 길이보다 추가되는 구간 길이가 길면, 예외를 던진다.")
    @Test
    void sectionDistanceException() {
        // given
        stationService.createStation(new StationRequest("A"));
        stationService.createStation(new StationRequest("B"));
        stationService.createStation(new StationRequest("C"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        // when then
        assertThatThrownBy(() -> {
            lineService.addSection(lineId, new LineRequest(2L, 3L, 200));
        }).isInstanceOf(SectionDistanceException.class);
    }

    @DisplayName("상행 종점역을 삭제한다. (A-B-C에서 A 삭제)")
    @Test
    void deleteUpTerminalSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        Line line = lineService.createLine(
            new LineRequest("2호선", "bg-green-600", stationA.getId(), stationB.getId(), 100));
        Long lineId = line.getId();
        lineService.addSection(lineId, new LineRequest(stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationA.getId());

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(1);
        assertThat(sections).containsExactlyInAnyOrder(new Section(2L, lineId, stationB, stationC, 50));
    }

    @DisplayName("하행 종점역을 삭제한다. (A-B-C에서 C 삭제)")
    @Test
    void deleteDownTerminalSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        Line line = lineService.createLine(
            new LineRequest("2호선", "bg-green-600", stationA.getId(), stationB.getId(), 100));
        Long lineId = line.getId();
        lineService.addSection(lineId, new LineRequest(stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationC.getId());

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(1);
        assertThat(sections).containsExactlyInAnyOrder(new Section(1L, lineId, stationA, stationB, 100));
    }

    @DisplayName("역사이에 있는 역을 삭제한다. (A-B-C에서 B 삭제)")
    @Test
    void deleteInternalSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        Line line = lineService.createLine(
            new LineRequest("2호선", "bg-green-600", stationA.getId(), stationB.getId(), 100));
        Long lineId = line.getId();
        lineService.addSection(lineId, new LineRequest(stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationB.getId());

        // then
        List<Section> sections = sectionRepository.findAllByLineId(lineId);
        assertThat(sections).hasSize(1);
        assertThat(sections)
            .containsExactlyInAnyOrder(new Section(3L, lineId, stationA, stationC, 150));
    }

    @DisplayName("구간이 하나밖에 존재하지 않는 노선에서 역을 삭제한다면, 예외를 던진다.")
    @Test
    void deleteDeleteSectionException() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));

        Line line = lineService.createLine(
            new LineRequest("2호선", "bg-green-600", stationA.getId(), stationB.getId(), 100));

        // when, then
        assertThatThrownBy(() -> {
            lineService.deleteSection(line.getId(), stationA.getId());
        }).isInstanceOf(DeleteSectionException.class);
    }

    @DisplayName("노선에 포함되지 않는 역을 삭제한다면, 예외를 던진다.")
    @Test
    void deleteNotExistStationException() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));

        Line line = lineService.createLine(
            new LineRequest("2호선", "bg-green-600", stationA.getId(), stationB.getId(), 100));

        // when, then
        assertThatThrownBy(() -> {
            lineService.deleteSection(line.getId(), stationC.getId());
        }).isInstanceOf(DeleteSectionException.class);
    }

    @DisplayName("상행부터 하행까지 정렬된 역 리스트를 반환한다.")
    @Test
    void findLineStations() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        Station stationC = stationService.createStation(new StationRequest("C"));
        Station stationD = stationService.createStation(new StationRequest("D"));
        Station stationE = stationService.createStation(new StationRequest("E"));
        Station stationF = stationService.createStation(new StationRequest("F"));
        Station stationG = stationService.createStation(new StationRequest("G"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 100);
        Line line = lineService.createLine(lineRequest);
        Long lineId = line.getId();

        lineService.addSection(lineId, new LineRequest(1L, 2L, 50));
        lineService.addSection(lineId, new LineRequest(3L, 5L, 25));
        lineService.addSection(lineId, new LineRequest(3L, 4L, 10));
        lineService.addSection(lineId, new LineRequest(5L, 7L, 10));
        lineService.addSection(lineId, new LineRequest(6L, 7L, 5));

        // when
        List<Station> stations = lineService.findSortedLineStations(1L);

        // then
        assertThat(stations).containsExactly(stationA, stationB, stationC, stationD, stationE, stationF, stationG);
    }

}
