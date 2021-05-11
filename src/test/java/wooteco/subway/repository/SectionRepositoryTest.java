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

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionRepositoryTest {

    private SectionRepository sectionRepository;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private long upStationId;
    private long downStationId;
    private long lastStationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(dataSource);
        stationDao = new StationDao(dataSource);
        sectionRepository = new SectionRepository(sectionDao, stationDao);

        String sectionSchemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        String stationSchemaQuery = "create table if not exists STATION ( id bigint auto_increment not null, nam varchar(255) " +
                "not null unique, primary key(id))";
        jdbcTemplate.execute(stationSchemaQuery);
        jdbcTemplate.execute(sectionSchemaQuery);

        upStationId = stationDao.save(new Station("천호역"));
        downStationId = stationDao.save(new Station("강남역"));
        lastStationId = stationDao.save(new Station("수원역"));
    }

    @DisplayName("Section을 등록 및 노선 아이디를 기준으로 조회한다.")
    @Test
    void saveAndFindByLineId() {
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Station lastStation = stationDao.findById(lastStationId).get();
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section secondSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionRepository.save(firstSection);
        long secondSectionId = sectionRepository.save(secondSection);
        List<Section> sections = sectionRepository.findAllByLineId(1L);

        assertThat(sections).contains(new Section(firstSectionId, upStation, downStation, 10, 1L),
                new Section(secondSectionId, downStation, lastStation, 5, 1L));
    }

    @DisplayName("Section을 등록 및 지하철역 아이디를 기준으로 조회한다.")
    @Test
    void saveAndFindByStationId() {
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Station lastStation = stationDao.findById(lastStationId).get();
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section secondSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionRepository.save(firstSection);
        long secondSectionId = sectionRepository.save(secondSection);
        List<Section> sections = sectionRepository.findAllByStationId(downStationId);

        assertThat(sections).contains(new Section(firstSectionId, upStation, downStation, 10, 1L),
                new Section(secondSectionId, downStation, lastStation, 5, 1L));
    }
}
