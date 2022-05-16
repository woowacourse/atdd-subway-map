package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.LineDaoImpl;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionDaoImpl;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.StationDaoImpl;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

@JdbcTest
@Sql("/init.sql")
public class SectionServiceTest {

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;
    private LineService lineService;
    private SectionService sectionService;

    @Autowired
    public SectionServiceTest(JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDaoImpl(jdbcTemplate);
        this.lineDao = new LineDaoImpl(jdbcTemplate);
        this.sectionDao = new SectionDaoImpl(jdbcTemplate);
        this.lineService = new LineService(stationDao, lineDao, sectionDao);
        this.sectionService = new SectionService(sectionDao);
    }

    @BeforeEach
    void beforeEach() {
        stationDao.insert(new Station("강남역"));
        stationDao.insert(new Station("역삼역"));
        stationDao.insert(new Station("선릉역"));
        stationDao.insert(new Station("정자역"));
        lineService.insertLine(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10));
    }

    @Test
    @DisplayName("구간을 추가할 수 있다.")
    void insertSection() {
        sectionService.insertSection(1L, new SectionRequest(2L, 3L, 10));

        assertThat(sectionDao.findByLineId(1L).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("구간을 제거할 수 있다.")
    void deleteSection() {
        sectionService.insertSection(1L, new SectionRequest(2L, 3L, 10));
        sectionService.deleteSection(1L, 2L);

        assertThat(sectionDao.findByLineId(1L).size()).isEqualTo(1);
    }
}
