package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql("/sectionDaoTestSchema.sql")
class JdbcSectionDaoTest {

    public static final Section GIVEN_SECTION = new Section(
            new Line(1L, "신분당선", "yellow"),
            new Station(1L, "신도림역"), new Station(2L, "왕십리역"),
            6, 1L);

    private final SectionDao sectionDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        sectionDao = new JdbcSectionDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("Section을 저장한다.")
    void saveSection() {
        Section section = GIVEN_SECTION;

        Long id = sectionDao.save(section);

        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 존재하는지 확인한다.")
    void existByStationIdAndLineId() {
        // given
        Long id = sectionDao.save(GIVEN_SECTION);

        // when
        boolean result = sectionDao.existByLineIdAndStationId(1L, 1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 존재하지 않는 지 확인한다.")
    void notExistByStationId() {
        // given
        Long id = sectionDao.save(GIVEN_SECTION);

        // when
        boolean result = sectionDao.existByLineIdAndStationId(1L, 3L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 상행으로 존재하면 해당 구간의 id를 가져온다.")
    void findIdByLineIdAndUpStationId() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        Optional<Long> id = sectionDao.findIdByLineIdAndUpStationId(1L, 1L);

        // then
        assertThat(id.get()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 상행으로 존재하지 않으면 해당 구간의 id를 null로 가져온다.")
    void findNullIdByLineIdAndUpStationId() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        Optional<Long> distance = sectionDao.findIdByLineIdAndUpStationId(1L, 2L);

        // then
        assertThat(distance).isNotPresent();
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 하행으로 존재하면 해당 구간의 id를 가져온다.")
    void findIdByLineIdAndDownStationId() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        Optional<Long> sectionId = sectionDao.findIdByLineIdAndDownStationId(1L, 2L);

        // then
        assertThat(sectionId.get()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 하행으로 존재하지 않으면 해당 구간의 id를 null로 가져온다.")
    void findNullIdByLineIdAndDownStationId() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        Optional<Long> id = sectionDao.findIdByLineIdAndDownStationId(1L, 1L);

        // then
        assertThat(id).isNotPresent();
    }

    @Test
    @DisplayName("id를 이용해서 거리를 가져온다.")
    void findDistanceById() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        int distance = sectionDao.findDistanceById(1L);

        // then
        assertThat(distance).isEqualTo(6);
    }

    @Test
    @DisplayName("id를 이용해서 lineOrder를 가져온다.")
    void findLineOrderById() {
        // given
        sectionDao.save(GIVEN_SECTION);

        // when
        Long lineOrder = sectionDao.findLineOrderById(1L);

        // then
        assertThat(lineOrder).isEqualTo(1L);
    }

    @Test
    @DisplayName("입력으로 들어온 값보다 lineOrder 값이 같거나 큰 구간들의 lineOrder 값을 1 증가시킨다.")
    void updateLineOrderById() {
        // given
        Long givenSectionId = sectionDao.save(GIVEN_SECTION);

        // when
        sectionDao.updateLineOrder(1L, 1L);

        // then
        assertThat(sectionDao.findLineOrderById(givenSectionId)).isEqualTo(2L);
    }

    @Test
    @DisplayName("입력으로 들어온 값보다 lineOrder 값이 작은 구간들의 lineOrder 값은 변화하지 않는다.")
    void notUpdateLineOrderById() {
        // given
        Long givenSectionId = sectionDao.save(GIVEN_SECTION);

        // when
        sectionDao.updateLineOrder(1L, 2L);

        // then
        assertThat(sectionDao.findLineOrderById(givenSectionId)).isEqualTo(1L);
    }
}
