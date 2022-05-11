package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Section;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
    }

    @DisplayName("지하철 노선 구간을 저장한다.")
    @Test
    void save() {
        Section section = new Section(10, 1L, 1L, 3L);
        Section savedSection = sectionDao.save(section);

        assertThat(section.getDistance()).isEqualTo(savedSection.getDistance());
    }

    @DisplayName("지하철 노선 구간 전체를 조회한다.")
    @Test
    void findAll() {
        //given
        Section section1 = new Section(10, 1L, 1L, 3L);
        Section section2 = new Section(10, 2L, 11L, 13L);
        Section section3 = new Section(10, 3L, 21L, 23L);
        sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.save(section3);

        //when
        List<Section> sections = sectionDao.findAll();

        //then
        assertThat(sections.size()).isEqualTo(3);
    }

    @DisplayName("지하철 노선 구간을 노선 Id로 조회한다.")
    @Test
    void findByLineId() {
        //given
        Section section1 = new Section(10, 1L, 1L, 3L);
        Section section2 = new Section(10, 1L, 11L, 13L);
        Section section3 = new Section(10, 2L, 21L, 23L);
        sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.save(section3);

        //when
        List<Section> sections = sectionDao.findByLineId(1L);

        //then
        assertThat(sections.size()).isEqualTo(2);
    }

    @DisplayName("Section과 정보가 동일한 section을 삭제한다")
    @Test
    void deleteBySection() {
        Section section1 = new Section(10, 1L, 1L, 3L);
        Section section2 = new Section(10, 1L, 11L, 13L);
        Section savedSection1 = sectionDao.save(section1);
        Section savedSection2 = sectionDao.save(section2);

        assertAll(() -> {
            assertThatCode(() -> sectionDao.deleteById(savedSection1.getId()))
                    .doesNotThrowAnyException();

            assertThat(sectionDao.findAll().size()).isOne();
        });
    }

}
