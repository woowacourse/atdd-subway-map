package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    private Long savedLineId;
    private Long savedStationId1;
    private Long savedStationId2;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);

        LineDao lineDao = new LineDao(jdbcTemplate);
        StationDao stationDao = new StationDao(jdbcTemplate);

        Line newLine = new Line("2호선", "bg-red-600");
        savedLineId = lineDao.save(newLine).getId();

        Station newStation1 = new Station("선릉역");
        savedStationId1 = stationDao.save(newStation1).getId();

        Station newStation2 = new Station("잠실역");
        savedStationId2 = stationDao.save(newStation2).getId();
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        // given
        Distance distance = new Distance(10);
        Section section = new Section(savedLineId, savedStationId1, savedStationId2, distance);

        // when
        Section createdSection = sectionDao.save(section);

        // then
        assertThat(createdSection).isNotNull();
    }
}
