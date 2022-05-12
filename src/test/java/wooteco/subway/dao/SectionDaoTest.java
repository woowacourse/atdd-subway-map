package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

    @DisplayName("특정 구간을 삭제할 수 있다")
    @Test
    void delete() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        dao.save(line.getSections(), line.getId());

        LineDao lineDao = new JdbcLineDao(dataSource, jdbcTemplate);
        Line updatedLine = lineDao.findById(line.getId());
        Section deletedSection = updatedLine.delete(station);
        assertThat(dao.delete(deletedSection)).isEqualTo(1);
    }

    @DisplayName("특정 노선의 구간을 모두 삭제할 수 있다")
    @Test
    void deleteByLine() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        dao.save(line.getSections(), line.getId());
        assertThat(dao.deleteByLine(line.getId())).isEqualTo(2);
    }

    @DisplayName("삭제할 구간이 없을 경우 예외가 발생한다")
    @Test
    void delete_no_data() {
        Section section = new Section(1L, upTermination, downTermination, 10);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> dao.delete(section))
                .withMessageContaining("존재하지 않습니다");
    }
}
