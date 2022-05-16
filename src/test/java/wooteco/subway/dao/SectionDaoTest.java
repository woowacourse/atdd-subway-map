package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import java.util.List;

@JdbcTest
class SectionDaoTest {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private Station station1;
    private Station station2;
    private Line line;

    @BeforeEach
    void setUp() {
        station1 = stationDao.save(new Station("아차산역"));
        station2 = stationDao.save(new Station("군자역"));
        line = lineDao.save(new Line("5호선", "bg-purple-600"));
    }

    @Autowired
    private SectionDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.stationDao = new StationDao(namedParameterJdbcTemplate);
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
        this.lineDao = new LineDao(namedParameterJdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        final Section section = new Section(station1, station2, 10, line.getId());
        final Section savedSection = sectionDao.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(section.getUpStation()),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(section.getDownStation())
        );
    }

    @DisplayName("구간을 모두 저장한다.")
    @Test
    void saveAll() {
        final Station station3 = stationDao.save(new Station("장한평역"));
        final Section section1 = new Section(station1, station2, 10, line.getId());

        final Section section2 = new Section(station2, station3, 5, line.getId());

        sectionDao.saveAll(List.of(section1, section2));

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isEqualTo(2);
    }

    @DisplayName("특정 라인의 모든 구간을 불러온다.")
    @Test
    void findAllByLineId() {
        final Station station3 = stationDao.save(new Station("광나루역"));

        final Section section1 = new Section(station1, station2, 10, line.getId());
        final Section section2 = new Section(station2, station3, 10, line.getId());
        sectionDao.save(section1);
        sectionDao.save(section2);

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isEqualTo(2);
    }

    @DisplayName("특정 라인의 모든 구간을 삭제한다.")
    @Test
    void deleteByLineId() {
        final Section section = new Section(station1, station2, 10, line.getId());
        sectionDao.save(section);

        sectionDao.deleteByLineId(line.getId());

        assertThat(sectionDao.findAllByLineId(line.getId()).size()).isEqualTo(0);
    }
}
