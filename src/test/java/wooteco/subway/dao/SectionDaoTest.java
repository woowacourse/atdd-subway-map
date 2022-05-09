package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void beforeEach() {
        sectionDao = new SectionDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 등록할 수 있다.")
    void save() {
        // given
        stationDao.save(new Station("지하철역이름"));
        stationDao.save(new Station("새로운지하철역이름"));
        final List<Station> stations = stationDao.findAll();
        final Long lineSaveID = lineDao.save(new Line("신분당선", "bg-red-600"));
        final Section section = new Section(lineSaveID, stations.get(0).getId(), stations.get(1).getId(), 10);

        // when
        sectionDao.save(section);

        // then
        final List<Section> responses = sectionDao.findAllByLineId(lineSaveID);
        assertThat(responses).hasSize(1);
    }
}
