package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
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
        sectionDao.save(new Section(1L, 1L, 2L, 10));
        sectionDao.save(new Section(1L, 2L, 3L, 5));
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

    @Test
    @DisplayName("Line-id 를 이용하여 section 을 조회한다.")
    void findByLineId() {
        //given
        Long lineId = 1L;
        //when
        List<Section> sections = sectionDao.findByLineId(lineId);
        long expectedIdCount = sections.stream()
                .filter(section -> section.getLineId().equals(lineId))
                .count();
        //then
        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(expectedIdCount).isEqualTo(2)
        );
    }

}