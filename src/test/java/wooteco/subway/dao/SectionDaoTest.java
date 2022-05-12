package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
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
    private StationDao stationDao;
    private LineDao lineDao;

    private final Station seolleungStation = new Station("선릉역");
    private final Station gangnamStation = new Station("강남역");
    private final Line line = new Line("2호선", "green");

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void save() {
        // given
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        Section section = new Section(lineId, upStationId, downStationId, 10);

        // when
        Long sectionId = sectionDao.save(section);

        // then
        assertThat(sectionId).isPositive();
    }

    @Test
    @DisplayName("lineId와 downStationId에 해당하는 구간이 존재하는지 확인한다.")
    void existByLineIdAndDownStationId() {
        // given
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        sectionDao.save(new Section(lineId, upStationId, downStationId, 10));

        // when
        boolean result = sectionDao.existByLineIdAndDownStationId(lineId, downStationId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("lineId와 upStationId에 해당하는 구간이 존재하는지 확인한다.")
    void existByLineIdAndUpStationId() {
        // given
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        sectionDao.save(new Section(lineId, upStationId, downStationId, 10));

        // when
        boolean result = sectionDao.existByLineIdAndUpStationId(lineId, upStationId);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("upStationId 또는 downStationId에 해당하는 구간이 존재하는 경우 반환한다.")
    void findByUpOrDownStationId() {
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        sectionDao.save(new Section(lineId, upStationId, downStationId, 10));

        // when
        Optional<Section> section = sectionDao.findByUpOrDownStationId(lineId, upStationId, 3L);

        // then
        assertThat(section).isPresent();
    }

    @Test
    @DisplayName("id에 해당하는 구간의 upStationId와 거리를 수정한다.")
    void updateUpStationId() {
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        Section section = new Section(lineId, upStationId, downStationId, 10);
        Long id = sectionDao.save(section);

        // when
        sectionDao.updateUpStationId(id, 3L, 50);

        // then
        Section actual = sectionDao.findById(id);
        Section expected = new Section(id, section.getLineId(), 3L, section.getDownStationId(), 50);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("id에 해당하는 구간의 downStationId와 거리를 수정한다.")
    void updateDownStationId() {
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        Section section = new Section(lineId, upStationId, downStationId, 10);
        Long id = sectionDao.save(section);

        // when
        sectionDao.updateDownStationId(id, 3L, 50);

        // then
        Section actual = sectionDao.findById(id);
        Section expected = new Section(id, section.getLineId(), section.getUpStationId(), 3L, 50);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("id에 해당하는 지하철 노선을 조회한다.")
    void findById() {
        // given
        Long upStationId = stationDao.save(seolleungStation);
        Long downStationId = stationDao.save(gangnamStation);
        Long lineId = lineDao.save(line);
        Section section = new Section(lineId, upStationId, downStationId, 10);
        Long id = sectionDao.save(section);

        // when
        Section actual = sectionDao.findById(id);

        // then
        Section expected = new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(),
                section.getDistance());
        assertThat(actual).isEqualTo(expected);
    }
}
