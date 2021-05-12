package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DeleteSectionException;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.DuplicatedSectionException;
import wooteco.subway.exception.NotContainStationsException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
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

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        Line line = new Line("2호선", "bg-green-600");
        Line result = lineService.createLine(line);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(line.getName());
        assertThat(result.getColor()).isEqualTo(line.getColor());
    }

    @DisplayName("중복된 이름을 갖는 노선을 생성하면, 예외가 발생한다.")
    @Test
    void createDuplicateLineException() {
        Line line = new Line("2호선", "bg-green-600");
        lineService.createLine(line);

        assertThatThrownBy(() -> lineService.createLine(line))
            .isInstanceOf(DuplicateLineNameException.class);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        lineService.createLine(new Line("2호선", "bg-green-600"));
        lineService.createLine(new Line("3호선", "bg-orange-600"));
        Line line = lineService.showLine(1L);

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("없는 노선을 조회하면, 예외가 발생한다.")
    @Test
    void showNotExistLineException() {
        assertThatThrownBy(() -> lineService.showLine(1L))
            .isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void showLines() {
        Line line2 = lineService.createLine(new Line("2호선", "bg-green-600"));
        Line line3 = lineService.createLine(new Line("3호선", "bg-orange-600"));
        Line line4 = lineService.createLine(new Line("4호선", "bg-skyBlue-600"));

        List<Line> lines = lineService.showLines();

        assertThat(lines).hasSize(3);
        assertThat(lines).containsExactly(line2, line3, line4);
    }

    @DisplayName("조회되는 노선 목록이 없으면, 예외가 발생한다.")
    @Test
    void showNotExistLinesException() {
        assertThatThrownBy(lineService::showLines).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line line2 = new Line("2호선", "bg-green-600");
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        lineService.createLine(line2);
        lineService.updateLine(newLine);

        assertThat(lineService.showLine(1L)).isEqualTo(newLine);
    }

    @DisplayName("없는 노선을 수정하면, 예외가 발생한다.")
    @Test
    void updateException() {
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        assertThatThrownBy(() -> {
            lineService.updateLine(newLine);
        }).isInstanceOf(NotExistLineException.class);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Line line2 = new Line("2호선", "bg-green-600");

        lineService.createLine(line2);

        assertThatCode(() -> lineService.deleteLine(1L)).doesNotThrowAnyException();
        assertThatThrownBy(lineService::showLines).isInstanceOf(NotExistLineException.class);
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
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationB.getId(), stationC.getId(), 100));

        // when
        lineService.addSection(new Section(lineId, stationA.getId(), stationB.getId(), 50));

        // then
        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(1L, lineId, stationB.getId(), stationC.getId(), 100),
            new Section(2L, lineId, stationA.getId(), stationB.getId(), 50)
        );
    }

    @DisplayName("노선에 하행 종점역을 추가한다. (A-B에 B-C 구간 추가시, A-B-C)")
    @Test
    void addDownTerminalSection() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));

        // when
        lineService.addSection(new Section(lineId, stationB.getId(), stationC.getId(), 50));

        // then
        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(1L, lineId, stationA.getId(), stationB.getId(), 100),
            new Section(2L, lineId, stationB.getId(), stationC.getId(), 50)
        );
    }

    @DisplayName("노선 중간에 역을 추가한다. (A-C에 A-B 구간 추가시, A-B-C)")
    @Test
    void addSection() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationC.getId(), 100));

        // when
        lineService.addSection(new Section(lineId, stationA.getId(), stationB.getId(), 50));

        // then
        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(3L, lineId, stationA.getId(), stationB.getId(), 50),
            new Section(2L, lineId, stationB.getId(), stationC.getId(), 50)
        );
    }

    @DisplayName("노선 중간에 역을 추가한다. (A-C에 B-C 구간 추가시, A-B-C)")
    @Test
    void addSection2() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationC.getId(), 100));

        // when
        lineService.addSection(new Section(lineId, stationB.getId(), stationC.getId(), 50));

        // then
        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).hasSize(2);
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(2L, lineId, stationA.getId(), stationB.getId(), 50),
            new Section(3L, lineId, stationB.getId(), stationC.getId(), 50)
        );
    }

    @DisplayName("상행부터 하행까지 정렬된 역 리스트를 반환한다.")
    @Test
    void findLineStations() {
        // given
        Station stationA = new Station("A");
        Station stationB = new Station("B");
        Station stationC = new Station("C");
        Station stationD = new Station("D");
        Station stationE = new Station("E");
        Station stationF = new Station("F");
        Station stationG = new Station("G");

        stationService.createStation(stationA);
        stationService.createStation(stationB);
        stationService.createStation(stationC);
        stationService.createStation(stationD);
        stationService.createStation(stationE);
        stationService.createStation(stationF);
        stationService.createStation(stationG);

        lineService.createLine(new Line("7호선", "bg-green-600"));
        sectionService.createSection(new Section(1L, 1L, 3L, 100));

        lineService.addSection(new Section(1L, 1L, 2L, 50));
        lineService.addSection(new Section(1L, 3L, 5L, 25));
        lineService.addSection(new Section(1L, 3L, 4L, 10));
        lineService.addSection(new Section(1L, 5L, 7L, 10));
        lineService.addSection(new Section(1L, 6L, 7L, 5));

        // when
        List<Station> stations = lineService.findSortedLineStations(1L);

        // then
        assertThat(stations)
            .containsExactly(stationA, stationB, stationC, stationD, stationE, stationF, stationG);
    }

    @DisplayName("중복되는 구간을 추가하면, 예외를 던진다. (A-B에 A-B 구간 추가시)")
    @Test
    void duplicatedSectionException() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));

        // when
        assertThatThrownBy(() -> {
            lineService.addSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));
        }).isInstanceOf(DuplicatedSectionException.class);

    }

    @DisplayName("추가하는 구간의 역이 둘 다 노선에 포함되지 않는다면, 예외를 던진다. (A-B에 X-Y 구간 추가시)")
    @Test
    void notContainStationsException() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationX = stationService.createStation(new Station("X"));
        Station stationY = stationService.createStation(new Station("Y"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationX.getId(), stationY.getId(), 100));

        // when
        assertThatThrownBy(() -> lineService
            .addSection(new Section(lineId, stationA.getId(), stationB.getId(), 100)))
            .isInstanceOf(NotContainStationsException.class);
    }

    @DisplayName("노선 중간에 역을 추가할시 기존 역사이 길이보다 추가되는 구간 길이가 길면, 예외를 던진다.")
    @Test
    void sectionDistanceException() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationC.getId(), 100));

        // when then
        assertThatThrownBy(() -> lineService
            .addSection(new Section(lineId, stationB.getId(), stationC.getId(), 200)))
            .isInstanceOf(SectionDistanceException.class);
    }

    @DisplayName("상행 종점역을 삭제한다. (A-B-C에서 A 삭제)")
    @Test
    void deleteUpTerminalSection() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));
        lineService.addSection(new Section(lineId, stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationA.getId());

        // then
        assertThat(lineService.findSortedLineStations(lineId)).containsExactly(stationB, stationC);

        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(2L, lineId, stationB.getId(), stationC.getId(), 50)
        );
    }

    @DisplayName("하행 종점역을 삭제한다. (A-B-C에서 C 삭제)")
    @Test
    void deleteDownTerminalSection() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));
        lineService.addSection(new Section(lineId, stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationC.getId());

        // then
        assertThat(lineService.findSortedLineStations(lineId)).containsExactly(stationA, stationB);

        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(1L, lineId, stationA.getId(), stationB.getId(), 100)
        );
    }

    @DisplayName("역사이에 있는 역을 삭제한다. (A-B-C에서 B 삭제)")
    @Test
    void deleteInternalSection() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));
        lineService.addSection(new Section(lineId, stationB.getId(), stationC.getId(), 50));

        // when
        lineService.deleteSection(lineId, stationB.getId());

        // then
        assertThat(lineService.findSortedLineStations(lineId)).containsExactly(stationA, stationC);

        List<Section> sections = sectionRepository.findByLineId(line.getId());
        assertThat(sections).containsExactlyInAnyOrder(
            new Section(3L, lineId, stationA.getId(), stationC.getId(), 150)
        );
    }

    @DisplayName("구간이 하나밖에 존재하지 않는 노선에서 역을 삭제한다면, 예외를 던진다.")
    @Test
    void deleteDeleteSectionException() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));

        // when, then
        assertThatThrownBy(() -> {
            lineService.deleteSection(lineId, stationA.getId());
        }).isInstanceOf(DeleteSectionException.class);
    }

    @DisplayName("노선에 포함되지 않는 역을 삭제한다면, 예외를 던진다.")
    @Test
    void deleteNotExistStationException() {
        // given
        Station stationA = stationService.createStation(new Station("A"));
        Station stationB = stationService.createStation(new Station("B"));
        Station stationC = stationService.createStation(new Station("C"));
        Station stationD = stationService.createStation(new Station("D"));

        Line line = lineService.createLine(new Line("테스트용 노선", "bg-green-600"));
        Long lineId = line.getId();
        sectionService.createSection(new Section(lineId, stationA.getId(), stationB.getId(), 100));
        sectionService.createSection(new Section(lineId, stationB.getId(), stationC.getId(), 100));

        // when, then
        assertThatThrownBy(() -> {
            lineService.deleteSection(lineId, stationD.getId());
        }).isInstanceOf(NotExistStationException.class);
    }

}
