package wooteco.subway.section.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.station.dao.H2StationDao;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("H2SectionDao 테스트")
@JdbcTest
class H2SectionDaoTest {
    private SectionDao sectionDao;
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        sectionDao = new H2SectionDao(jdbcTemplate);
        stationDao = new H2StationDao(jdbcTemplate);
    }

    @AfterEach
    public void cleanDB() {
        sectionDao.deleteAll();
        stationDao.deleteAll();
        jdbcTemplate.update("ALTER TABLE STATION ALTER COLUMN `id` RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE SECTION ALTER COLUMN `id` RESTART WITH 1");
    }

    @DisplayName("Section을 테이블에 저장한다.")
    @Test
    public void save() {
        //given
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "역삼역");
        Section section = new Section(1L, upStation, downStation, new Distance(10));

        //when
        Section newSection = sectionDao.save(section);
        System.out.println(newSection.getId());

        //then
        assertThat(newSection).isEqualTo(new Section(1L, 1L, upStation, downStation, new Distance(10)));
    }

    @DisplayName("테이블에 있는 Section을 line_id로 조회한다.")
    @Test
    public void findByLineId() {
        //given
        Station upStation1 = new Station(1L, "강남역");
        Station downStation1 = new Station(2L, "역삼역");
        Section section1 = new Section(1L, upStation1, downStation1, new Distance(10));

        Station upStation2 = new Station(2L, "역삼역");
        Station downStation2 = new Station(3L, "잠실역");
        Section section2 = new Section(1L, upStation2, downStation2, new Distance(20));

        stationDao.save(upStation1);
        stationDao.save(downStation1);
        stationDao.save(downStation2);

        //when
        Section newSection1 = sectionDao.save(section1);
        Section newSection2 = sectionDao.save(section2);
        Sections sections = sectionDao.findByLineId(1L);

        //then
        assertThat(sections.sections()).containsExactly(newSection1, newSection2);
    }

    @DisplayName("테이블에 있는 Section 데이터를 수정한다")
    @Test
    public void update() {
        //given
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "잠실역");
        Station station3 = new Station(3L, "역삼역");
        Section section = new Section(1L, station1, station2, new Distance(10));
        Section newSection = sectionDao.save(section);

        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        //when
        Section updateSection = new Section(newSection.getId(), newSection.getLineId(),
                station1, station3, new Distance(5));
        sectionDao.update(updateSection);

        //then
        assertThat(sectionDao.findById(newSection.getId())).isEqualTo(updateSection);
    }

    @DisplayName("테이블에 있는 Section 데이터를 삭제한다")
    @Test
    public void delete() {
        //given
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "잠실역");
        Section section = new Section(1L, station1, station2, new Distance(10));
        Section newSection = sectionDao.save(section);

        //when
        sectionDao.delete(newSection);

        //then
        assertThatThrownBy(() -> sectionDao.findById(section.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("데이터베이스에 해당 ID의 구간이 없습니다.");
    }
}