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
public class JdbcSectionDaoTest {
    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        lineDao = new JdbcLineDao(jdbcTemplate);
        stationDao = new JdbcStationDao(jdbcTemplate);
        sectionDao = new JdbcSectionDao(jdbcTemplate, lineDao, stationDao);
        createTables();

        final Line line = new Line("분당선", "bg-red-600");
        final Station upStation = new Station("지하철역이름");
        final Station downStation = new Station("새로운지하철역이름");
        final Section section = new Section(line, upStation, downStation, 10);
        Long lineId = lineDao.save(line);
        line.setId(lineId);
        Long upStationId = stationDao.save(upStation);
        upStation.setId(upStationId);
        Long downStationId = stationDao.save(downStation);
        downStation.setId(downStationId);
        sectionDao.save(section);
    }

    @Test
    @DisplayName("지하철 구간을 저장한다.")
    void save() {
        final String sql = "SELECT COUNT(*) FROM SECTION";
        final int expected = 1;

        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철 구간을 삭제한다.")
    void delete() {
        // given
        final Line line = new Line("신분당선", "bg-blue-600");
        final Station upStation = new Station("지하철역");
        final Station downStation = new Station("새로운지하철역");
        final Section section2 = new Section(line, upStation, downStation, 10);
        Long lineId = lineDao.save(line);
        line.setId(lineId);
        Long upStationId = stationDao.save(upStation);
        upStation.setId(upStationId);
        Long downStationId = stationDao.save(downStation);
        downStation.setId(downStationId);
        Long stationId = sectionDao.save(section2);

        // when
        sectionDao.deleteById(stationId);
        final int expected = 1;

        // then
        final String sql = "SELECT COUNT(*) FROM SECTION";
        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }

    private void createTables() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS LINE(\n"
                + "    id BIGINT AUTO_INCREMENT NOT NULL,\n"
                + "    name VARCHAR(255) NOT NULL UNIQUE,\n"
                + "    color VARCHAR(20) NOT NULL,\n"
                + "    PRIMARY KEY(id)\n"
                + ");");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS STATION(\n"
                + "    id BIGINT AUTO_INCREMENT NOT NULL,\n"
                + "    name VARCHAR(255) NOT NULL UNIQUE,\n"
                + "    PRIMARY KEY(id)\n"
                + ");");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SECTION(\n"
                + "    id BIGINT AUTO_INCREMENT NOT NULL,\n"
                + "    line_id BIGINT NOT NULL,\n"
                + "    up_station_id BIGINT NOT NULL,\n"
                + "    down_station_id BIGINT NOT NULL,\n"
                + "    distance int NOT NULL,\n"
                + "    PRIMARY KEY (id),\n"
                + "    FOREIGN KEY (line_id) REFERENCES LINE(id) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (up_station_id) REFERENCES STATION(id) ON DELETE CASCADE,\n"
                + "    FOREIGN KEY (down_station_id) REFERENCES STATION(id) ON DELETE CASCADE\n"
                + ");");
    }
}
