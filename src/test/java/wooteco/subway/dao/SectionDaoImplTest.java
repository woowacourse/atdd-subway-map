package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

public class SectionDaoImplTest extends DaoImplTest {

    private SectionDaoImpl sectionDaoImpl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDaoImpl = new SectionDaoImpl(jdbcTemplate);
    }

    @DisplayName("구간 정보를 저장한다.")
    @Test
    void save() {
        Section section = new Section(1L, 1L, 2L, 12);
        Section newSection = sectionDaoImpl.save(section);

        assertThat(newSection.getLineId()).isEqualTo(1L);
        assertThat(newSection.getUpStationId()).isEqualTo(1L);
        assertThat(newSection.getDownStationId()).isEqualTo(2L);
        assertThat(newSection.getDistance()).isEqualTo(12);
    }

    @DisplayName("같은 노선 아이디를 가지는 구간 정보를 전부 조회한다.")
    @Test
    void findByLineId() {
        Section firstSection = new Section(1L, 1L, 2L, 12);
        Section secondSection = new Section(1L, 1L, 2L, 12);
        Section thirdSection = new Section(1L, 1L, 2L, 12);

        sectionDaoImpl.save(firstSection);
        sectionDaoImpl.save(secondSection);
        sectionDaoImpl.save(thirdSection);
        List<Section> sections = sectionDaoImpl.findByLineId(firstSection.getLineId());

        sections.forEach(section -> assertThat(section.getLineId()).isEqualTo(firstSection.getLineId()));
    }
}
