package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.section.Section;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 jdbc 테스트")
@JdbcTest
@TestPropertySource("classpath:application.yml")
@Sql("classpath:initialize.sql")
class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcSectionDao jdbcSectionDao;

    @BeforeEach
    void setUp() {
        this.jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 구간 생성")
    @Test
    void save() {
        // given
        Section section = new Section(1L, 1L, 2L, 3);

        // when
        Section newSection = jdbcSectionDao.save(section);

        // then
        assertThat(newSection).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, 1L, 2L, 3));
    }

    @DisplayName("호선 Id에 따른 모든 구간 조회")
    @Test
    void findAllByLineId() {
        // given
        Long lineId = 1L;
        Section oneToTwo = new Section(1L, 1L, 2L, 3);
        Section TwoToThree = new Section(1L, 2L, 3L, 3);

        // when
        jdbcSectionDao.save(oneToTwo);
        jdbcSectionDao.save(TwoToThree);
        List<Section> sections = jdbcSectionDao.findAllByLineId(lineId);

        // then
        assertThat(sections).usingRecursiveComparison()
                .isEqualTo(Arrays.asList(
                        new Section(1L, 1L, 1L, 2L, 3),
                        new Section(2L, 1L, 2L, 3L, 3)
                ));
    }

    @DisplayName("upStationId와 distance 업데이트")
    @Test
    void updateUpStation() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        jdbcSectionDao.save(section);
        jdbcSectionDao.updateUpStation(section, 3L);
        List<Section> sections = jdbcSectionDao.findAllByLineId(1L);

        // then
        assertThat(sections.get(0)).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, 3L, 2L, 3));
    }

    @DisplayName("downStationId와 distance 업데이트")
    @Test
    void updateDownStation() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        jdbcSectionDao.save(section);
        jdbcSectionDao.updateDownStation(section, 3L);
        List<Section> sections = jdbcSectionDao.findAllByLineId(1L);

        // then
        assertThat(sections.get(0)).usingRecursiveComparison()
                .isEqualTo(new Section(1L, 1L, 1L, 3L, 3));
    }

    @DisplayName("lineId와 upStationId로 구간 제거")
    @Test
    void deleteByLineIdAndUpStationId() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        jdbcSectionDao.save(section);
        jdbcSectionDao.deleteByLineIdAndUpStationId(1L, 1L);
        List<Section> sections = jdbcSectionDao.findAllByLineId(1L);

        // then
        assertThat(sections).hasSize(0);
    }

    @DisplayName("lineId와 downStationId로 구간 제거")
    @Test
    void deleteByLineIdAndDownStationId() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        jdbcSectionDao.save(section);
        jdbcSectionDao.deleteByLineIdAndDownStationId(1L, 2L);
        List<Section> sections = jdbcSectionDao.findAllByLineId(1L);

        // then
        assertThat(sections).hasSize(0);
    }

    @DisplayName("section객체에 해당하는 구간 삭제")
    @Test
    void deleteBySection() {
        // given
        Section section = new Section(1L, 1L, 1L, 2L, 3);

        // when
        jdbcSectionDao.save(section);
        jdbcSectionDao.deleteBySection(section);
        List<Section> sections = jdbcSectionDao.findAllByLineId(1L);

        // then
        assertThat(sections).hasSize(0);
    }
}