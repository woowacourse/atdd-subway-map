package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixtures.GANGNAM;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SINSA;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("지하철 구간을 저장한다.")
    void save() {
        final Long upStationId = stationDao.save(new Station(HYEHWA));
        final Long downStationId = stationDao.save(new Station(SINSA));
        final Long lineId = lineDao.save(new Line(LINE_2, RED));
        final Section section = new Section(lineId, upStationId, downStationId, 10);

        final Long id = sectionDao.save(section);
        final Section savedSection = sectionDao.findById(id);

        assertAll(() -> {
            assertThat(savedSection.getId()).isNotNull();
            assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId());
            assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId());
            assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
        });
    }

    @Test
    @DisplayName("지하철 역 ID로 모든 구간을 조회한다.")
    void findAllByLineId() {
        final Long stationId1 = stationDao.save(new Station(HYEHWA));
        final Long stationId2 = stationDao.save(new Station(SINSA));
        final Long stationId3 = stationDao.save(new Station(GANGNAM));
        final Long lineId = lineDao.save(new Line(LINE_2, RED));
        final Section section1 = new Section(lineId, stationId1, stationId2, 10);
        final Section section2 = new Section(lineId, stationId2, stationId3, 10);

        sectionDao.save(section1);
        sectionDao.save(section2);

        final Sections sections = sectionDao.findAllByLineId(lineId);

        assertThat(sections.getSections()).hasSize(2);
    }

    @Test
    @DisplayName("지하철 구간 ID로 해당 구간을 조회한다.")
    void findById() {
        // given
        final Long upStationId = stationDao.save(new Station(HYEHWA));
        final Long downStationId = stationDao.save(new Station(SINSA));
        final Long lineId = lineDao.save(new Line(LINE_2, RED));
        final Section section = new Section(lineId, upStationId, downStationId, 10);
        final Long id = sectionDao.save(section);

        // when
        final Section savedSection = sectionDao.findById(id);

        // then
        assertAll(() -> {
            assertThat(savedSection.getLineId()).isEqualTo(lineId);
            assertThat(savedSection.getUpStationId()).isEqualTo(upStationId);
            assertThat(savedSection.getDownStationId()).isEqualTo(downStationId);
            assertThat(savedSection.getDistance()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("지하철 구간 ID로 구간을 삭제한다.")
    void deleteById() {
        // given
        final Long upStationId = stationDao.save(new Station(HYEHWA));
        final Long downStationId = stationDao.save(new Station(SINSA));
        final Long lineId = lineDao.save(new Line(LINE_2, RED));
        final Long sectionId = sectionDao.save(new Section(lineId, upStationId, downStationId, 10));

        // when
        sectionDao.deleteById(sectionId);

        // then
        assertThat(sectionDao.findAllByLineId(lineId).getSections()).hasSize(0);
    }
}
