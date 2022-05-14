package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
class SectionJdbcDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    private Long lineId1;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private int distance;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionJdbcDao(jdbcTemplate);
        stationDao = new StationJdbcDao(jdbcTemplate);
        lineDao = new LineJdbcDao(jdbcTemplate);

        lineId1 = lineDao.save(new Line("name", "color"));
        stationId1 = stationDao.save(new Station(lineId1, "name1"));
        stationId2 = stationDao.save(new Station(lineId1, "name2"));
        stationId3 = stationDao.save(new Station(lineId1, "name3"));
        stationId4 = stationDao.save(new Station(lineId1, "name4"));
        distance = 10;
    }

    @DisplayName("새로운 구간을 저장한다.")
    @Test
    void save() {
        // given
        Long lineId = lineId1;
        Long upStationId = stationId1;
        Long downStationId = stationId2;

        Section section = new Section(lineId, upStationId, downStationId, distance);

        // when
        Long savedId = sectionDao.save(section);

        // then
        Section actual = sectionDao.findById(savedId);
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(savedId),
                () -> assertThat(actual.getLineId()).isEqualTo(lineId),
                () -> assertThat(actual.getUpStationId()).isEqualTo(upStationId),
                () -> assertThat(actual.getDownStationId()).isEqualTo(downStationId),
                () -> assertThat(actual.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("상하행 역이 없으면 새로운 구간을 저장할 수 없다.")
    @Test
    void saveNotHasStation() {
        // given
        Long lineId = lineId1;
        Long upStationId = 100L;
        Long downStationId = 200L;

        Section section = new Section(lineId, upStationId, downStationId, distance);

        // when
        // then
        assertThatThrownBy(() -> sectionDao.save(section))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("상하행 노선이 없으면 새로운 구간을 저장할 수 없다.")
    @Test
    void saveNotHasLine() {
        // given
        Long lineId = 300L;
        Long upStationId = stationId1;
        Long downStationId = stationId2;

        Section section = new Section(lineId, upStationId, downStationId, distance);

        // when
        // then
        assertThatThrownBy(() -> sectionDao.save(section))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("구간 정보를 수정한다.")
    @Test
    void update() {
        // given
        Long lineId = lineId1;
        Long upStationId = stationId1;
        Long downStationId = stationId2;

        Section section = new Section(lineId, upStationId, downStationId, distance);
        Long savedId = sectionDao.save(section);

        Long upStationId2 = stationId3;
        Long downStationId2 = stationId4;
        int distance2 = distance;
        Section section2 = new Section(lineId1, upStationId2, downStationId2, distance2);

        // when
        Long updatedId = sectionDao.update(savedId, section2);

        // then
        Section actual = sectionDao.findById(updatedId);
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(savedId),
                () -> assertThat(actual.getLineId()).isEqualTo(lineId),
                () -> assertThat(actual.getUpStationId()).isEqualTo(upStationId2),
                () -> assertThat(actual.getDownStationId()).isEqualTo(downStationId2),
                () -> assertThat(actual.getDistance()).isEqualTo(distance2)
        );
    }

    @DisplayName("특정 id를 가지는 구간을 조회한다.")
    @Test
    void findById() {
        // given
        Long lineId = lineId1;
        Long upStationId = stationId1;
        Long downStationId = stationId2;

        Section section = new Section(lineId, upStationId, downStationId, distance);
        Long savedId = sectionDao.save(section);

        // when
        Section actual = sectionDao.findById(savedId);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(savedId),
                () -> assertThat(actual.getLineId()).isEqualTo(lineId),
                () -> assertThat(actual.getUpStationId()).isEqualTo(upStationId),
                () -> assertThat(actual.getDownStationId()).isEqualTo(downStationId),
                () -> assertThat(actual.getDistance()).isEqualTo(distance)
        );
    }

    @DisplayName("특정 Line id를 가지는 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        // given
        Long lineId = lineId1;
        Long upStationId1 = stationId1;
        Long downStationId1 = stationId2;
        int distance1 = distance;
        Section section1 = new Section(lineId, upStationId1, downStationId1, distance1);
        Long savedId1 = sectionDao.save(section1);

        Long upStationId2 = stationId3;
        Long downStationId2 = stationId4;
        int distance2 = distance;
        Section section2 = new Section(lineId, upStationId2, downStationId2, distance2);
        Long savedId2 = sectionDao.save(section2);

        // when
        List<Section> actual = sectionDao.findAllByLineId(lineId);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual.get(0).getId()).isEqualTo(savedId1),
                () -> assertThat(actual.get(1).getId()).isEqualTo(savedId2)
        );
    }

    @DisplayName("모든 구간에서 역 id를 찾는다")
    @Test
    void findAllByStationId() {
        // given
        Long save1 = sectionDao.save(new Section(lineId1, stationId1, stationId2, distance));
        Long save2 = sectionDao.save(new Section(lineId1, stationId2, stationId3, distance));

        // when
        List<Section> actual = sectionDao.findAllByStationId(stationId2);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual.get(0).getUpStationId()).isEqualTo(stationId1),
                () -> assertThat(actual.get(1).getDownStationId()).isEqualTo(stationId3)
        );
    }

    @DisplayName("특정 Line id를 가지는 모든 구간을 제거한다.")
    @Test
    void deleteAllByLineId() {
        // given
        Long lineId = lineId1;
        Long upStationId1 = stationId1;
        Long downStationId1 = stationId2;
        int distance1 = distance;
        Section section1 = new Section(lineId, upStationId1, downStationId1, distance1);
        Long savedId1 = sectionDao.save(section1);

        Long upStationId2 = stationId3;
        Long downStationId2 = stationId4;
        int distance2 = distance;
        Section section2 = new Section(lineId, upStationId2, downStationId2, distance2);
        Long savedId2 = sectionDao.save(section2);

        LineDao lineDao = new LineJdbcDao(jdbcTemplate);
        Long lineId2 = lineDao.save(new Line("name2", "color2"));
        Long upStationId3 = stationId1;
        Long downStationId3 = stationId2;
        int distance3 = distance;
        Section section3 = new Section(lineId2, upStationId3, downStationId3, distance3);
        Long savedId3 = sectionDao.save(section3);

        // when
        sectionDao.deleteAllByLineId(lineId);

        // then
        assertThat(sectionDao.findAllByLineId(lineId)).isEmpty();
        assertThat(sectionDao.findAllByLineId(lineId2)).hasSize(1);
    }
}
