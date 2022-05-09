package wooteco.subway.dao.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class JdbcSectionDaoTest {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        stationDao = new JdbcStationDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate);
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 추가한다")
    void createSection(){
        Line line = lineDao.create(new Line("2호선", "green"));
        Station station1 = stationDao.create(new Station("강남역"));
        Station station2 = stationDao.create(new Station("선릉역"));

        Section section = sectionDao.create(new Section(line.getId(), station1.getId(), station2.getId(), 10));

        assertAll(
                () -> assertThat(section.getId()).isEqualTo(line.getId()),
                () -> assertThat(section.getUpStationId()).isEqualTo(station1.getId()),
                () -> assertThat(section.getDownStationId()).isEqualTo(station2.getId())
        );
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection(){
        //given
        Line line = lineDao.create(new Line("1호선", "blue"));
        Station station1 = stationDao.create(new Station("구로"));
        Station station2 = stationDao.create(new Station("신도림"));
        Section section = sectionDao.create(new Section(line.getId(), station1.getId(), station2.getId(), 10));
        //when
        sectionDao.delete(section.getId());
        //then
        assertThat(sectionDao.existById(section.getId())).isFalse();
    }
}