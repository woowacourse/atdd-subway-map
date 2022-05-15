package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import javax.sql.DataSource;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@JdbcTest
@Sql("/schema.sql")
class SectionServiceTest {

    private final SectionService sectionService;
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    @Autowired
    public SectionServiceTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        lineDao = new LineDao(jdbcTemplate, dataSource);
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        stationDao = new StationDao(jdbcTemplate, dataSource);
        sectionService = new SectionService(lineDao, sectionDao, stationDao);
    }

    @BeforeEach
    void setUp() {
        lineDao.save(new Line("신분당선", "red"));
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("역삼역"));
    }

    @Test
    @DisplayName("구간을 등록한다")
    void add() {
        sectionDao.save(new Section(1L, 1L, 2L, 5));
        assertThatCode(() -> sectionService.add(1L, new SectionRequest(2L, 3L, 5)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간을 제거한다")
    void delete() {
        sectionDao.save(new Section(1L, 1L, 2L, 5));
        sectionDao.save(new Section(1L, 2L, 3L, 5));

        sectionService.delete(1L, 2L);

        assertThat(sectionDao.findByLineId(1L)).hasSize(1)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(Tuple.tuple(1L, 3L, 10));
    }
}
