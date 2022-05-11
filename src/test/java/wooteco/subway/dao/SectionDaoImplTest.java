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
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql("classpath:sectionDao.sql")
public class SectionDaoImplTest {

    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    private Section section;
    private Line line;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDaoImpl(dataSource);
        lineDao = new LineDaoImpl(dataSource);
        stationDao = new StationDaoImpl(dataSource);

        stationDao.save(new Station("서울역"));
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));

        line = lineDao.save(new Line("분당선", "red"));

        section = sectionDao.save(new Section(line.getId(), 1L, 2L, 10));
    }

    @DisplayName("새로운 구간을 추가한다.")
    @Test
    void save() {
        lineDao.save(new Line("신분당선", "orange"));

        assertThatCode(() ->
                sectionDao.save(new Section(2L, 2L, 3L, 5)))
                .doesNotThrowAnyException();
    }

    @DisplayName("구간을 변경한다.")
    @Test
    void update() {
        sectionDao.update(new Section(section.getId(), line.getId(), 3L, 2L, 5));

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

    @DisplayName("section에 해당하는 구간을 삭제한다.")
    @Test
    void delete() {
        section = sectionDao.save(new Section(1L, 2L, 3L, 5));

        sectionDao.delete(section.getId());
        List<Section> sections = sectionDao.findByLineId(1L);

        assertThat(sections).hasSize(1);
    }

    @DisplayName("ids에 해당하는 모든 구간을 삭제한다.")
    @Test
    void deleteByIds() {
        section = sectionDao.save(new Section(1L, 2L, 3L, 5));
        List<Long> ids = List.of(1L, 2L);

        sectionDao.deleteByIds(ids);

        List<Section> sections = sectionDao.findByLineId(1L);
        assertThat(sections).hasSize(0);
    }
}
