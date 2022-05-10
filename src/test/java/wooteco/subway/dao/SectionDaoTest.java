package wooteco.subway.dao;

import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;
    private SectionDao sectionDao;
    private LineDao lineDao;

    @BeforeEach
    void beforeEach() {
        sectionDao = new SectionDao(dataSource);
        lineDao = new LineDao(dataSource);
        stationDao = new StationDao(dataSource);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void save() {
        // given
        Station 신림역 = stationDao.save(new Station("신림역"));
        Station 봉천역 = stationDao.save(new Station("봉천역"));
        Line line = lineDao.save(new Line("test", "GREEN"));
        Section section = new Section(신림역, 봉천역, 10);
        // when
        Long save = sectionDao.save(line.getId(), section);
        // then
        Assertions.assertThat(save).isEqualTo(1L);
    }
}
