package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql("classpath:sectionDao.sql")
public class SectionDaoImplTest {

    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;
    private Section section;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(dataSource);
        section = sectionDao.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("새로운 구간을 추가한다.")
    @Test
    void save() {
        assertThatCode(() ->
                sectionDao.save(new Section(2L, 2L, 3L, 5)))
                .doesNotThrowAnyException();
    }

    @DisplayName("구간을 변경한다.")
    @Test
    void update() {
        sectionDao.update(new Section(1L, 1L, 3L, 2L, 5));

        List<Section> sections = sectionDao.findByLineId(1L);

        Section newSection = sections.get(0);

        assertThat(newSection.getUpStationId()).isEqualTo(3L);
        assertThat(newSection.getDownStationId()).isEqualTo(2L);
    }

    @DisplayName("lineId 값에 해당하는 모든 구간의 정보를 가져온다.")
    @Test
    void findByLineId() {
        section = sectionDao.save(new Section(1L, 2L, 3L, 5));
        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).hasSize(2);
    }
}
