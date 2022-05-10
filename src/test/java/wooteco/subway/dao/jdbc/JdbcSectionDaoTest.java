package wooteco.subway.dao.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcSectionDaoTest {

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        stationDao = new JdbcStationDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate);
        sectionDao = new JdbcSectionDao(jdbcTemplate);

        lineDao.create(new Line("2호선", "bg-green-300"));
        stationDao.create(new Station("낙성대"));
        stationDao.create(new Station("교대"));
        stationDao.create(new Station("선릉"));
    }

    @Test
    @DisplayName("구간을 추가한다")
    void createSection(){
        //given
        //when
        Section actual = sectionDao.create(new Section(1L, 1L, 2L, 10));
        Section expected = new Section(1L, 1L, 1L, 2L, 10);
        //then
        assertThat(expected)
                .usingRecursiveComparison()
                .isEqualTo(actual);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection(){
        //given
        Section section1 = sectionDao.create(new Section(1L, 1L, 2L, 10));
        Section section2 = sectionDao.create(new Section(1L, 2L, 3L, 20));
        //when
        sectionDao.delete(section1.getId());
        //then
        assertThat(sectionDao.existById(section1.getId())).isFalse();
    }
}