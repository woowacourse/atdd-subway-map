package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({SectionDao.class, StationDao.class})
class SectionDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    private Station firstStation;
    private Station secondStation;
    private Station thirdStation;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("SET foreign_key_checks=0;");
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
        jdbcTemplate.execute("insert into LINE (name, color) values ('9호선', '황토')");
        jdbcTemplate.execute("SET foreign_key_checks=1;");
        firstStation = stationDao.save("가양역");
        secondStation = stationDao.save("증미역");
        thirdStation = stationDao.save("등촌역");
        stationDao.save("염창역");
        stationDao.save("신목동역");
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
    @DisplayName("노선에 등록된 구간 수 확인")
    public void isExistingLine() {
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(0);
        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
    }

    @Test
    @DisplayName("라인 ID로 구간 조회")
    public void findSectionsByLineId() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        List<Section> sections = sectionDao.findAllByLineId(1L);

        assertThat(sections).containsExactlyInAnyOrderElementsOf(
                Arrays.asList(
                        new Section(1L, 1L, firstStation, secondStation, 10),
                        new Section(2L, 1L, secondStation, thirdStation, 10))
        );
    }

    @Test
    @DisplayName("구간 삭제 확인")
    public void delete() {
        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
        sectionDao.delete(1L);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(0);

        sectionDao.save(1L, 1L, 2L, 10);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
        sectionDao.delete(1L, 1L);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(0);
    }

    @Test
    @DisplayName("구간 수정 확인")
    public void update() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.updateDistanceAndDownStation(1L, 1L, 3L, 20);

        SectionResponse sectionResponse = jdbcTemplate.queryForObject(
                "select line_id, up_station_id, down_station_id, distance from SECTION where id=1",
                (rs, rowNum) ->
                        new SectionResponse(
                                1L,
                                rs.getLong("line_id"),
                                rs.getLong("up_station_id"),
                                rs.getLong("down_station_id"),
                                rs.getInt("distance")
                        )
        );

        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(1L, 1L, 1L, 3L, 20));

    }
}