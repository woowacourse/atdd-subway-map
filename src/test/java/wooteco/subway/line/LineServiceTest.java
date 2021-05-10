package wooteco.subway.line;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.section.Section;
import wooteco.subway.section.SectionService;
import wooteco.subway.station.Station;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;

    @DisplayName("노선을 생성하고 반환한다.")
    @Test
    void createLine() {
        Line line = new Line("2호선", "red");
        Line createdLine = lineService.createLine(line);
        assertEquals(1L, createdLine.getId());
        assertEquals("2호선", createdLine.getName());
        assertEquals("red", createdLine.getColor());
        assertEquals(Collections.emptyList(), createdLine.getStations());
    }

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

    @Test
    void showLine() {
        List<Station> stations = Arrays.asList(
            new Station(1L, "강남역"),
            new Station(2L, "잠실역"),
            new Station(3L, "성수역"));
        Line line = new Line("2호선", "red");
        lineService.createLine(new Line(line, stations));

        //TODO: mock..
        // sectionService.createSection(new Section())

        assertEquals("강남역", lineService.showLine(1L).getStations().get(0).getName());
        assertEquals("잠실역", lineService.showLine(1L).getStations().get(1).getName());
        assertEquals("성수역", lineService.showLine(1L).getStations().get(2).getName());
    }

    @Test
    void updateLine() {
    }

    @Test
    void deleteLine() {
    }
}