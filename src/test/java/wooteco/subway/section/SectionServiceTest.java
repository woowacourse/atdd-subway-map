package wooteco.subway.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.section.exception.SectionCantDeleteException;
import wooteco.subway.section.exception.SectionDistanceException;
import wooteco.subway.section.exception.SectionInclusionException;
import wooteco.subway.section.exception.SectionInitializationException;
import wooteco.subway.station.StationDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

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
        stationDao.save("first");
        stationDao.save("second");
        stationDao.save("third");
        stationDao.save("fourth");
    }

    @Test
    @DisplayName("정상적인 중간 구간 저장")
    public void saveSectionWithNormalCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 4L, 1));
        sectionService.save(sectionDto);

        SectionResponse sectionResponse = findByUpStationId(2L);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(3L, 1L, 2L, 4L, 1));

        SectionResponse sectionResponse2 = findByUpStationId(4L);
        assertThat(sectionResponse2)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(4L, 1L, 4L, 3L, 9));
    }

    @Test
    @DisplayName("등록되지 않은 노선에 구간을 저장하는 경우 확인")
    public void saveSectionWithNonExistingLineCase() {
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 4L, 1));
        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionInitializationException.class);
    }

    @Test
    @DisplayName("역 사이의 거리가 기존 구간의 거리 이상일 경우의 중간 구간 저장")
    public void saveSectionWithDistanceExceptionCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 4L, 11));
        SectionDto sectionDto2 = SectionDto.of(1L, new SectionRequest(4L, 2L, 11));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionDistanceException.class);
        assertThatThrownBy(() -> sectionService.save(sectionDto2))
                .isInstanceOf(SectionDistanceException.class);
    }

    @Test
    @DisplayName("상행 종점 구간 등록")
    public void saveSectionWithUpEndStationCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(4L, 1L, 1));
        sectionService.save(sectionDto);

        SectionResponse sectionResponse = findByUpStationId(4L);

        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(2L, 1L, 4L, 1L, 1));
    }

    @Test
    @DisplayName("하행 종점 구간 등록")
    public void saveSectionWithDownEndStationCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(2L, 4L, 1));
        sectionService.save(sectionDto);

        SectionResponse sectionResponse = findByUpStationId(2L);

        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(2L, 1L, 2L, 4L, 1));
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 모두 포함될 경우의 구간 등록")
    public void saveSectionWithBothStationContainCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(1L, 2L, 1));
        SectionDto sectionDto2 = SectionDto.of(1L, new SectionRequest(2L, 1L, 1));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionInclusionException.class);
        assertThatThrownBy(() -> sectionService.save(sectionDto2))
                .isInstanceOf(SectionInclusionException.class);
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 아무것도 포함된 것이 없을 경우의 구간 등록")
    public void saveSectionWithNeitherStationContainCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        SectionDto sectionDto = SectionDto.of(1L, new SectionRequest(3L, 4L, 1));

        assertThatThrownBy(() -> sectionService.save(sectionDto))
                .isInstanceOf(SectionInclusionException.class);
    }

    @Test
    @DisplayName("종점 역이 포함된 구간 삭제")
    public void deleteSectionWithContainingEndStationCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        sectionService.delete(1L, 1L);

        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
        assertThatThrownBy(() -> findByUpStationId(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @DisplayName("구간이 1개 이하인 경우의 삭제")
    public void deleteSectionWithLessThantOneSectionCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        assertThatThrownBy(() -> sectionService.delete(1L, 1L))
                .isInstanceOf(SectionCantDeleteException.class);
    }

    @Test
    @DisplayName("등록된 노선이 아닌 경우의 삭제")
    public void deleteSectionWithNonExistingLineCase() {
        assertThatThrownBy(() -> sectionService.delete(1L, 1L))
                .isInstanceOf(SectionInitializationException.class);
    }

    @Test
    @DisplayName("중간 구간을 삭제한 경우")
    public void deleteSectionWithMiddleSectionCase() {
        sectionDao.save(1L, 1L, 2L, 10);
        sectionDao.save(1L, 2L, 3L, 10);
        sectionDao.save(1L, 3L, 4L, 10);

        sectionService.delete(1L, 2L);
        SectionResponse sectionResponse = findByUpStationId(1L);

        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(1L, 1L, 1L, 3L, 20));
    }

    private SectionResponse findByUpStationId(Long upStationId) {
        return jdbcTemplate.queryForObject(
                "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = 1 and up_station_id = ?",
                (rs, num) -> new SectionResponse(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        upStationId,
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                ), upStationId
        );
    }
}