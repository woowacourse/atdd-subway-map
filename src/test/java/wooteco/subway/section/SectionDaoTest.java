package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SectionDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    private Station firstStation;
    private Station secondStation;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        firstStation = stationDao.save("FirstStation");
        secondStation = stationDao.save("SecondStation");
        stationDao.save("ThirdStation");
        stationDao.save("FourthStation");
        stationDao.save("FifthStation");
    }

    @Test
    @DisplayName("구간 추가 확인")
    public void save() {
        Section savedSection = sectionDao.save(1L, 1L, 2L, 10);
        assertThat(savedSection)
                .usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, firstStation, secondStation, 10));
    }

    @Test
    @DisplayName("구간 삭제 확인")
    public void delete() {
        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.findSectionByUpStationId(1L, 1L).isPresent()).isTrue();

        sectionDao.delete(1L);
        assertThat(sectionDao.findSectionByUpStationId(1L, 1L).isPresent()).isFalse();
    }

    @Test
    @DisplayName("구간에 존재하는 역인지 확인")
    public void isExistingStation() {
        assertThat(sectionDao.isExistingStation(1L, 1L)).isFalse();

        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.isExistingStation(1L, 1L)).isTrue();
        assertThat(sectionDao.isExistingStation(1L, 2L)).isTrue();
        assertThat(sectionDao.isExistingStation(2L, 1L)).isFalse();
        assertThat(sectionDao.isExistingStation(2L, 2L)).isFalse();
    }

    @Test
    @DisplayName("역 ID로 구간 검색")
    public void findSectionByStationId() {
        sectionDao.save(1L, 1L, 2L, 10);

        assertThat(sectionDao.findSectionByUpStationId(1L, 1L).isPresent()).isTrue();
        assertThat(sectionDao.findSectionByUpStationId(1L, 2L).isPresent()).isFalse();

        assertThat(sectionDao.findSectionByDownStationId(1L, 2L).isPresent()).isTrue();
        assertThat(sectionDao.findSectionByDownStationId(1L, 1L).isPresent()).isFalse();
    }

    @Test
    @DisplayName("구간에 포함된 역 중 종점역이 존재하는지 확인")
    public void isStationEndStiation() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        sectionDao.save(1L, 3L, 4L, 10);

        assertThat(sectionDao.hasEndStationInSection(1L, 1L, 2L)).isTrue();
        assertThat(sectionDao.hasEndStationInSection(1L, 2L, 3L)).isFalse();
        assertThat(sectionDao.hasEndStationInSection(1L, 3L, 4L)).isTrue();
        assertThat(sectionDao.hasEndStationInSection(1L, 4L, 5L)).isTrue();
    }

    @Test
    @DisplayName("노선에 등록된 구간 수 확")
    public void isExistingLine() {
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(0);
        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
    }
}