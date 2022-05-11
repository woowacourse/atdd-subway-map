package wooteco.subway.dao;

import javax.sql.DataSource;
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
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SectionDao dao;

    private final Station upTermination = new Station(1L, "상행종점역");
    private final Station downTermination = new Station(2L, "하행종점역");
    private final Station station = new Station(3L, "추가역");
    private Line line;

    @BeforeEach
    void setUp() {
        dao = new JdbcSectionDao(dataSource, jdbcTemplate);
        StationDao stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
        stationDao.save(upTermination);
        stationDao.save(downTermination);
        stationDao.save(station);

        Section section = new Section(upTermination, downTermination, 10);
        LineDao lineDao = new JdbcLineDao(dataSource, jdbcTemplate);
        line = new Line("신분당선", "bg-red-600", section);
        lineDao.save(line);
    }

    @DisplayName("기존 노선에 구간을 추가할 수 있다")
    @Test
    void save_sections() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        dao.save(line.getSections(), line.getId());
    }
}
