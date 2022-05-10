package wooteco.subway.dao;

import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
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
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate, dataSource);
        stationDao = new StationDao(jdbcTemplate, dataSource);
        lineDao = new LineDao(jdbcTemplate, dataSource);
    }

    @DisplayName("구간 정보를 저장한다.")
    @Test
    void save() {
        Station jamsil = stationDao.save(new Station("잠실역"));
        Station seonreong = stationDao.save(new Station("선릉역"));
        Line twoLine = lineDao.save(new Line("2호선", "연두색"));

        Section expected = new Section(jamsil.getId(), seonreong.getId(), twoLine.getId(), 10);
        Section actual = sectionDao.save(expected);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
