package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.SectionEntity;

@JdbcTest
public class JdbcSectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private JdbcSectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Section 을 저장한다.")
    void save() {
        //when
        Long actual = sectionDao.save(new SectionEntity(1L, 1L, 2L, 5));

        //then
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    @DisplayName("Line id 로 Section 을 조회한다.")
    void findByLineId() {
        //given
        Long sectionIdA = sectionDao.save(new SectionEntity(1L, 1L, 2L, 5));
        Long sectionIdB = sectionDao.save(new SectionEntity(1L, 2L, 3L, 5));

        //when
        List<SectionEntity> actual = sectionDao.findByLineId(1L);
        List<SectionEntity> expected = List.of(new SectionEntity(sectionIdA, 1L, 1L, 2L, 5),
            new SectionEntity(sectionIdB, 1L, 2L, 3L, 5));

        //then
        assertThat(actual).isEqualTo(expected);
    }
}
