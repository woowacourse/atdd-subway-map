package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;
    private LineDao lineDao;
    private Long lineId;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(dataSource);
        sectionDao = new SectionDao(dataSource);

        Line line = new Line("2호선", "초록색");
        lineId = lineDao.save(line).getId();
    }

    @Test
    @DisplayName("section을 저장하고 id가 추가된 section을 반환한다.")
    void saveSection() {
        Section section = new Section(lineId, 1L, 2L, 3);

        Section persistSection = sectionDao.save(section);

        assertThat(persistSection.getId()).isNotNull();
        assertThat(persistSection.getUpStationId()).isEqualTo(1L);
        assertThat(persistSection.getDownStationId()).isEqualTo(2L);
        assertThat(persistSection.getDistance()).isEqualTo(3);
    }

    @Test
    @DisplayName("lineId에 해당되는 모든 section을 반환한다.")
    void findByLineId() {
        Section section1 = new Section(lineId, 1L, 2L, 3);
        Section section2 = new Section(lineId, 2L, 3L, 3);
        Section section3 = new Section(lineId, 3L, 4L, 3);
        sectionDao.save(section1);
        sectionDao.save(section2);
        sectionDao.save(section3);

        List<Section> sections = sectionDao.findByLineId(lineId);

        assertThat(sections.size()).isEqualTo(3);
    }
}
