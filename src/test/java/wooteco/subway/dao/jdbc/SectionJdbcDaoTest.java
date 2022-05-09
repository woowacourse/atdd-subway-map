package wooteco.subway.dao.jdbc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@JdbcTest
class SectionJdbcDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionJdbcDao(jdbcTemplate);
    }

    @Sql(value = "/sql/InsertTwoStationAndOneLine.sql")
    @DisplayName("새로운 구간을 저장한다.")
    @Test
    void save() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;
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

    @Sql(value = "/sql/InsertTwoStationAndOneLine.sql")
    @DisplayName("구간 정보를 수정한다.")
    @Test
    void update() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;
        Section section = new Section(lineId, upStationId, downStationId, distance);
        Long savedId = sectionDao.save(section);

        Long lineId2 = 1L;
        Long upStationId2 = 1L;
        Long downStationId2 = 2L;
        int distance2 = 10;
        Section sectio2 = new Section(lineId2, upStationId2, downStationId2, distance2);

        // when
        Long updatedId = sectionDao.update(savedId, sectio2);

        // then
        Section actual = sectionDao.findById(updatedId);
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(savedId),
                () -> assertThat(actual.getLineId()).isEqualTo(lineId2),
                () -> assertThat(actual.getUpStationId()).isEqualTo(upStationId2),
                () -> assertThat(actual.getDownStationId()).isEqualTo(downStationId2),
                () -> assertThat(actual.getDistance()).isEqualTo(distance2)
        );
    }

    @Sql(value = "/sql/InsertTwoStationAndOneLine.sql")
    @DisplayName("특정 id를 가지는 구간을 조회한다.")
    @Test
    void findById() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;
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

    @Sql(value = "/sql/InsertTwoStationAndOneLine.sql")
    @DisplayName("특정 Line id를 가지는 모든 구간을 조회한다.")
    @Test
    void findAllByLineId() {
        // given
        Long lineId = 1L;
        Long upStationId1 = 1L;
        Long downStationId1 = 2L;
        int distance1 = 10;
        Section section1 = new Section(lineId, upStationId1, downStationId1, distance1);
        Long savedId1 = sectionDao.save(section1);

        Long upStationId2 = 1L;
        Long downStationId2 = 2L;
        int distance2 = 10;
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

    @Sql(value = "/sql/InsertTwoStationAndOneLine.sql")
    @DisplayName("특정 Line id를 가지는 모든 구간을 제거한다.")
    @Test
    void deleteAllByLineId() {
        // given
        Long lineId = 1L;
        Long upStationId1 = 1L;
        Long downStationId1 = 2L;
        int distance1 = 10;
        Section section1 = new Section(lineId, upStationId1, downStationId1, distance1);
        Long savedId1 = sectionDao.save(section1);

        Long upStationId2 = 1L;
        Long downStationId2 = 2L;
        int distance2 = 10;
        Section section2 = new Section(lineId, upStationId2, downStationId2, distance2);
        Long savedId2 = sectionDao.save(section2);

        // when
        sectionDao.deleteAllByLineId(lineId);

        // then
        assertThat(sectionDao.findAllByLineId(lineId)).isEmpty();
    }
}
