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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Station upStation = new Station(1L, "천호역");
    private Station downStation = new Station(2L, "강남역");
    private Station lastStation = new Station(3L, "강릉역");

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        String schemaQuery = "create table if not exists SECTION ( id bigint auto_increment not null, " +
                "line_id bigint not null, up_station_id bigint not null, down_station_id bigint not null, " +
                "distance int, primary key(id) )";
        jdbcTemplate.execute(schemaQuery);
    }

    @DisplayName("구간을 등록 및 조회한다.")
    @Test
    void save() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);
        Section lastSection = new Section(downStation, lastStation, 5, 1L);

        long firstSectionId = sectionDao.save(firstSection);
        long lastSectionId = sectionDao.save(lastSection);
        List<Section> sections = sectionDao.findAllByLineId(1L);

        assertThat(sections).containsExactly(new Section(firstSectionId, 10, 1L),
                new Section(lastSectionId, 5, 1L));
    }

    @DisplayName("구간에 등록된 역들의 아이디를 반환한다.")
    @Test
    void findStationIdsById() {
        Section firstSection = new Section(upStation, downStation, 10, 1L);

        long id = sectionDao.save(firstSection);
        List<Long> stationIds = sectionDao.findStationIdsById(id);

        assertThat(stationIds).containsExactly(1L, 2L);
    }
}
