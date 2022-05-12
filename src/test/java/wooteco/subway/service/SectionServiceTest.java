package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import javax.sql.DataSource;
import org.assertj.core.groups.Tuple;
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

    @Test
    @DisplayName("구간을 등록한다")
    void add() {
        long lineId = lineDao.save(new Line("신분당선", "red"));
        long stationId1 = stationDao.save(new Station("강남역"));
        long stationId2 = stationDao.save(new Station("선릉역"));
        long stationId3 = stationDao.save(new Station("역삼역"));
        sectionDao.save(new Section(lineId, stationId1, stationId2, 5));

        assertThatCode(() -> sectionService.add(lineId, new SectionRequest(stationId2, stationId3, 5)))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간을 제거한다")
    void delete() {
        long lineId = lineDao.save(new Line("신분당선", "red"));
        long stationId1 = stationDao.save(new Station("강남역"));
        long stationId2 = stationDao.save(new Station("선릉역"));
        long stationId3 = stationDao.save(new Station("역삼역"));
        sectionDao.save(new Section(lineId, stationId1, stationId2, 5));
        sectionDao.save(new Section(lineId, stationId2, stationId3, 5));

        sectionService.delete(lineId, stationId2);

        assertThat(sectionDao.findByLineId(lineId)).hasSize(1)
                .extracting("upStationId", "downStationId", "distance")
                .containsExactly(Tuple.tuple(stationId1, stationId3, 10));
    }
}
