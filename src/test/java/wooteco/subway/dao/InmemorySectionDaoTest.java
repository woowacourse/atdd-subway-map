package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class InmemorySectionDaoTest {

    private final InmemorySectionDao sectionDao = InmemorySectionDao.getInstance();
    private final InmemoryStationDao stationDao = InmemoryStationDao.getInstance();
    private final InmemoryLineDao lineDao = InmemoryLineDao.getInstance();

    @AfterEach
    void afterEach() {
        sectionDao.clear();
        stationDao.clear();
        lineDao.clear();
    }

    @Test
    @DisplayName("Section 을 저장할 수 있다.")
    void save() {
        // given
        Station upStation = new Station(1L, "오리");
        Station downStation = new Station(2L, "배카라");
        Section section = new Section(null, 1L, upStation, downStation, 1);

        // when
        Section savedSection = sectionDao.save(section);

        // then
        assertThat(savedSection.getId()).isNotNull();
    }

    @Test
    @DisplayName("Line Id에 해당하는 Section을 조회할 수 있다.")
    void findAllByLineId() {
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));
        Station station1 = stationDao.save(new Station("오리"));
        Station station2 = stationDao.save(new Station("배카라"));
        Station station3 = stationDao.save(new Station("오카라"));

        List<Section> expected = List.of(sectionDao.save(new Section(null, line.getId(), station1, station2, 1)),
                sectionDao.save(new Section(null, line.getId(), station2, station3, 1)));

        assertThat(sectionDao.findAllByLineId(line.getId())).isEqualTo(expected);
    }
}
