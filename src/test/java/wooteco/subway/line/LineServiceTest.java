package wooteco.subway.line;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.NoSuchLineException;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    private List<Station> stations;

    @BeforeEach
    void setUp() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("서초역");
        Station station3 = new Station("잠실역");
        Station station4 = new Station("매봉역");

        stations = Arrays.asList(station1, station2, station3, station4);
        stationService.createStation("강남역");
        stationService.createStation("서초역");
        stationService.createStation("잠실역");
        stationService.createStation("매봉역");
    }

    @DisplayName("노선을 생성하고 반환한다.")
    @Test
    void createLine() {
        Line line = new Line("2호선", "red");
        Line createdLine = lineService.createLine(new Line(line, stations));
        assertEquals(1L, createdLine.getId());
        assertEquals("2호선", createdLine.getName());
        assertEquals("red", createdLine.getColor());
        assertEquals(Collections.emptyList(), createdLine.getStations());
    }

    @DisplayName("생선된 노선들을 조회한다.")
    @Test
    void showLines() {
        Line line1 = new Line("2호선", "red");
        Line line2 = new Line("3호선", "orange");
        Line line3 = new Line("4호선", "yellow");
        Line createdLine1 = lineService.createLine(line1);
        Line createdLine2 = lineService.createLine(line2);
        Line createdLine3 = lineService.createLine(line3);

        assertTrue(lineService.showLines().containsAll(Arrays.asList(createdLine1, createdLine2, createdLine3)));
    }

    @DisplayName("하나의 노선과 관련된 정보를 조회한다.")
    @Test
    void showLine() {
        Line line = new Line("2호선", "red");
        Line createdLine = lineService.createLine(new Line(line, stations));

        Section section = new Section(createdLine.getId(), 1L, 2L, 100);
        Section section1 = new Section(createdLine.getId(), 2L, 3L, 100);
        assertEquals(1L, sectionService.createSection(section));
        assertEquals(2L, sectionService.createSection(section1));

        assertEquals("강남역", lineService.showLine(1L).getStations().get(0).getName());
        assertEquals("서초역", lineService.showLine(1L).getStations().get(1).getName());
        assertEquals("잠실역", lineService.showLine(1L).getStations().get(2).getName());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        showLine();
        lineService.updateLine(1L, new Line("바뀐호선", "black"));
        assertEquals("바뀐호선", lineService.showLine(1L).getName());
        assertEquals("black", lineService.showLine(1L).getColor());
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void updateLineException() {
        showLine();
        assertThatThrownBy(() -> lineService.updateLine(2L, new Line("바뀐호선", "black"))).isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        showLine();
        assertDoesNotThrow(() -> lineService.deleteLine(1L));
        assertThatThrownBy(() -> lineService.deleteLine(4L)).isInstanceOf(NoSuchLineException.class);
    }
}