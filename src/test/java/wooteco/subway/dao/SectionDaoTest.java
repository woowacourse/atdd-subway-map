package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
        String schemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        jdbcTemplate.execute(schemaQuery);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void save() {
        long upStationId = stationDao.save(new Station("강남역"));
        long downStationId = stationDao.save(new Station("천호역"));
        long lastStationId = stationDao.save(new Station("강릉역"));
        Station upStation = stationDao.findById(upStationId).get();
        Station downStation = stationDao.findById(downStationId).get();
        Station lastStation = stationDao.findById(lastStationId).get();
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section lastSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionDao.save(firstSection);
        long lastSectionId = sectionDao.save(lastSection);
        List<Section> sections = sectionDao.findAllByLineId(1L);

        assertThat(sections).containsExactly(new Section(firstSectionId, 10, 1L),
                new Section(lastSectionId, 5, 1L));
    }
}
