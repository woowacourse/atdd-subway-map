package wooteco.subway.section.dao;

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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("Section을 테이블에 저장한다.")
    @Test
    public void save() {
        //given
        Station upStation = new Station(1L, "강남역");
        Station downStation = new Station(2L, "역삼역");
        Section section = new Section(1L, Arrays.asList(upStation, downStation), new Distance(10));

        //when
        Section newSection = sectionDao.save(section);

        //then
        assertThat(newSection).isEqualTo(new Section(1L, 1L, Arrays.asList(upStation, downStation), new Distance(10)));
    }

    @DisplayName("테이블에 있는 Section을 line_id로 조회한다.")
    @Test
    public void findByLineId() {
        //given
        Station upStation1 = new Station(1L, "강남역");
        Station downStation1 = new Station(2L, "역삼역");
        Section section1 = new Section(1L, Arrays.asList(upStation1, downStation1), new Distance(10));

        Station upStation2 = new Station(2L, "역삼역");
        Station downStation2 = new Station(3L, "잠실역");
        Section section2 = new Section(1L, Arrays.asList(upStation2, downStation2), new Distance(20));

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


}