package wooteco.subway.section;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.exception.ImpossibleDistanceException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineService;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationService;

@SpringBootTest
@Transactional
@Sql("classpath:schema.sql")
class SectionServiceTest {
    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        Station station1 = new Station("강남역");
        Station station2 = new Station("서초역");
        Station station3 = new Station("잠실역");
        Station station4 = new Station("매봉역");

        stationService.createStation("강남역");
        stationService.createStation("서초역");
        stationService.createStation("잠실역");
        stationService.createStation("매봉역");

        lineService.createLine(new Line(
            new Line("2호선", "green"), Arrays.asList(station1, station2, station3, station4)));
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        Section section = new Section(1L, 2L, 100);
        Section section1 = new Section(2L, 3L, 100);
        assertEquals(1L, sectionService.createSection(section));
        assertEquals(2L, sectionService.createSection(section1));
    }


    @DisplayName("구간 추가 시 거리가 맞지 않으면 에러가 발생한다.")
    @Test
    void addSectionException() {
        Section section = new Section(1L, 1L, 3L, 100);
        Section section1 = new Section(1L, 2L, 3L, 1000);
        assertEquals(1L, sectionService.createSection(section));
        assertThatThrownBy(() -> sectionService.addSection(section1)).isInstanceOf(ImpossibleDistanceException.class);
    }

    @DisplayName("상행종점, 하행종점을 추가한다.")
    @Test
    void addEndSection() {
        Section section = new Section(1L, 1L, 2L, 100);
        Section endSection = new Section(1L, 2L, 3L, 1000);
        Section startSection = new Section(1L, 4L, 1L, 1000);

        assertEquals(1L, sectionService.createSection(section));
        assertEquals(2L, sectionService.addSection(endSection));
        assertEquals(3L, sectionService.addSection(startSection));
    }
}