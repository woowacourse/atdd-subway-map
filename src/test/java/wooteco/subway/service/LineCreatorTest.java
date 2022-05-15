package wooteco.subway.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

public class LineCreatorTest {
    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;
    private LineCreator lineCreator;

    @BeforeEach
    void setUp() {
        lineDao = new FakeLineDao();
        sectionDao = new FakeSectionDao();
        stationDao = new FakeStationDao();
        lineCreator = new LineCreator(lineDao, sectionDao, stationDao);

        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));
        lineDao.save(new Line("2호선", "green"));
        sectionDao.save(1L, new Section(new Station(1L, "강남역"), new Station(2L, "선릉역"), 10));
    }

    @DisplayName("정상적으로 Line을 만드는지 테스트")
    @Test
    void createLine() {
        Line line = lineCreator.createLine(1L);
        Sections sections = line.getSections();

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");

        assertThat(sections.size()).isEqualTo(1);

        List<Station> stations = sections.getStations();
        assertThat(stations.contains(new Station(1L, "강남역"))).isTrue();
        assertThat(stations.contains(new Station(2L, "선릉역"))).isTrue();
    }
}
