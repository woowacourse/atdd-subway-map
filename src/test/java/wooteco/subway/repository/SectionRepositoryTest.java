package wooteco.subway.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionRepositoryTest {

    private SectionRepository sectionRepository;
    private SectionDao sectionDao;
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        sectionRepository = new SectionRepository(sectionDao, stationDao);
        String sectionSchemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        String stationSchemaQuery = "create table if not exists STATION ( id bigint auto_increment not null, nam varchar(255) " +
                "not null unique, primary key(id))";
        jdbcTemplate.execute(stationSchemaQuery);
        jdbcTemplate.execute(sectionSchemaQuery);
    }

    @DisplayName("Section을 등록 및 조회한다.")
    @Test
    void save() {
        long upStationId = stationDao.save(new Station("천호역"));
        long downStationId = stationDao.save(new Station("강남역"));
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Section section = new Section(upStation, downStation, 10, 1L);

        sectionRepository.save(section);
        int counts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SECTION", int.class);

        assertThat(counts).isEqualTo(1);
    }
}
