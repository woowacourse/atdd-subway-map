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
import wooteco.subway.domain.Station;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;
    private SectionDao sectionDao;
    private LineDao lineDao;

    @BeforeEach
    void beforeEach() {
        sectionDao = new SectionDao(dataSource);
        lineDao = new LineDao(dataSource);
        stationDao = new StationDao(dataSource);
    }

    @DisplayName("구간을 등록하고 조회하고 삭제한다.")
    @Test
    void saveAndFindAll() {
        // given
        Station 신림역 = stationDao.save(new Station("신림역"));
        Station 봉천역 = stationDao.save(new Station("봉천역"));
        Station 서울대입구역 = stationDao.save(new Station("서울대입구역"));
        Station 낙성대역 = stationDao.save(new Station("낙성대역"));

        Line line = lineDao.save(new Line("test", "GREEN"));

        Section section = new Section(1L, 신림역, 봉천역, 10);
        Section section2 = new Section(2L, 봉천역, 서울대입구역, 10);
        Section section3 = new Section(3L, 서울대입구역, 낙성대역, 10);
        // when
        sectionDao.save(line.getId(), section);
        sectionDao.save(line.getId(), section2);
        sectionDao.save(line.getId(), section3);

        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        // then
        assertThat(sections).hasSize(3);
        assertThat(sections.get(0).getUpStation()).isEqualTo(신림역);
        assertThat(sections.get(1).getUpStation()).isEqualTo(봉천역);
        assertThat(sections.get(2).getUpStation()).isEqualTo(서울대입구역);

        sectionDao.remove(section3);
        List<Section> removedSections = sectionDao.findAllByLineId(line.getId());

        assertThat(removedSections).hasSize(2);
        assertThat(removedSections.get(0).getUpStation()).isEqualTo(신림역);
        assertThat(removedSections.get(1).getUpStation()).isEqualTo(봉천역);
    }
}
