package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    private final Station seolleungStation = new Station("선릉역");
    private final Station gangnamStation = new Station("강남역");
    private final Line line = new Line("2호선", "green");

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void save() {
        // given
        Long stationId1 = stationDao.save(seolleungStation);
        Long stationId2 = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        Section section = new Section(lineId, stationId1, stationId2, 10);

        // when
        Long sectionId = sectionDao.save(section);

        // then
        assertThat(sectionId).isPositive();
    }
}
