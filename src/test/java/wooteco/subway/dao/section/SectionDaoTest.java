package wooteco.subway.dao.section;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.station.JdbcStationDao;
import wooteco.subway.dao.station.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

@JdbcTest
class SectionDaoTest {

    private static final Station a = new Station(1L, "a역");
    private static final Station b = new Station(2L,"b역");
    private static final Station c = new Station(3L,"c역");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
        stationDao = new JdbcStationDao(jdbcTemplate);
    }

    @Test
    void dependency() {
        assertThat(sectionDao).isNotNull();
    }

    @Test
    void save() {
        // given
        Long lineId = 0L;
        Station savedStation1 = stationDao.save(a);
        Station savedStation2 = stationDao.save(b);

        // when
        Section section = new Section(lineId, savedStation1, savedStation2, 2);
        Section savedSection = sectionDao.save(section);

        // then
        assertThat(savedSection.getUpStation()).isEqualTo(savedStation1);
        assertThat(savedSection.getDownStation()).isEqualTo(savedStation2);
        assertThat(savedSection.getLineId()).isEqualTo(lineId);
        assertThat(savedSection.getDistance()).isEqualTo(2);
    }

    @Test
    void findById() {
        // given
        Long lineId = 0L;
        Station savedStation1 = stationDao.save(a);
        Station savedStation2 = stationDao.save(b);

        // when
        Section section = new Section(lineId, savedStation1, savedStation2, 2);
        Section savedSection = sectionDao.save(section);

        // then
        assertThat(sectionDao.findById(savedSection.getId()).get()).isEqualTo(savedSection);
    }

    @Test
    void findByLindId() {
        // given
        Long lineId = 0L;
        Station savedStation1 = stationDao.save(a);
        Station savedStation2 = stationDao.save(b);
        Station savedStation3 = stationDao.save(c);

        // when
        Section section1 = new Section(lineId, savedStation1, savedStation2, 2);
        Section section2 = new Section(lineId, savedStation2, savedStation3, 2);
        Section savedSection1 = sectionDao.save(section1);
        Section savedSection2 = sectionDao.save(section2);

        // then
        assertThat(sectionDao.findByLineId(lineId))
            .contains(savedSection1, savedSection2);
    }

    @Test
    void update() {
        // given
        Long lindId = 0L;
        Station savedStation1 = stationDao.save(a);
        Station savedStation2 = stationDao.save(b);
        Station savedStation3 = stationDao.save(c);

        Section section = new Section(lindId, savedStation1, savedStation2, 2);
        Section savedSection = sectionDao.save(section);

        // when
        Section updateSection = new Section(savedSection.getId(), lindId, savedStation2, savedStation3, 1);
        sectionDao.update(updateSection);

        // then
        assertThat(sectionDao.findById(savedSection.getId()).get()).isEqualTo(updateSection);
    }

    @Test
    void delete() {
        // given
        Long lindId = 0L;
        Station savedStation1 = stationDao.save(a);
        Station savedStation2 = stationDao.save(b);

        Section section = new Section(lindId, savedStation1, savedStation2, 2);
        Section savedSection = sectionDao.save(section);

        // when
        sectionDao.deleteById(savedSection.getId());

        // then
        assertThat(sectionDao.findById(savedSection.getId()).isPresent()).isFalse();
    }
}