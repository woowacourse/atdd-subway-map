package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
@Transactional
class SectionDaoTest {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;
    private Station station1;
    private Station station2;
    private Line line;

    @Autowired
    private SectionDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
        this.lineDao = new LineDao(namedParameterJdbcTemplate);
        this.stationDao = new StationDao(namedParameterJdbcTemplate);
    }

    @BeforeEach
    void setUp() {
        station1 = stationDao.save(new Station("아차산역"));
        station2 = stationDao.save(new Station("군자역"));
        line = lineDao.save(new Line("5호선", "bg-purple-600"));
    }

    @DisplayName("특정 노선의 구간을 저장한다.")
    @Test
    void save() {
        final Section section = new Section(station1.getId(), station2.getId(), 10, line.getId());
        final Section savedSection = sectionDao.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId()),
                () -> assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId())
        );
    }
}
