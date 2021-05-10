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
}