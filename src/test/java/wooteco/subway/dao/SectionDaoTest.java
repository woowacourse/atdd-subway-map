package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("section 을 저장한다.")
    void save() {
        //given
        Section section = new Section(1L, 1L, 2L, 10);
        //when
        Long sectionId = sectionDao.save(section);
        //then
        assertThat(sectionId).isNotNull();
    }

}