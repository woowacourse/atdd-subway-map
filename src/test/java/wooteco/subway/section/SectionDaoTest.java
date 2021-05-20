package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;


    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
        LineDao lineDao = new LineDao(jdbcTemplate);
        StationDao stationDao = new StationDao(jdbcTemplate);

        stationDao.save(new Station("테스트 역1"));
        stationDao.save(new Station("테스트 역2"));
        stationDao.save(new Station("테스트 역3"));

        lineDao.save(new Line(1L, "테스트 라인1", "BLACK"));

        sectionDao.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("구간 추가 성공 테스트")
    @Test
    void successSaveTest() {
        assertDoesNotThrow(() ->
            sectionDao.save(new Section(1L, 1, 2, 10))
        );
    }

    @DisplayName("구간 조회 테스트")
    @Test
    void selectSectionTest() {
        assertDoesNotThrow(() ->
            sectionDao.findBySectionId(1L)
        );
    }

    @DisplayName("존재하지 않는 라인의 구간 조회 테스트")
    @Test
    void failSelectSectionTest() {
        assertThatThrownBy(() ->
            sectionDao.findBySectionId(2L)
        ).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("구간 조회 크기 테스트")
    @Test
    void findAllStationSize() {
        sectionDao.save(new Section(1L, 2L, 3L, 20));
        List<Section> stations = sectionDao.findSectionsByLineId(1L);
        assertThat(stations).hasSize(2);
        System.out.println(stations);
    }

    @DisplayName("구간에 존재하는 역 확인 테스트")
    @Test
    void findExistStation() {
        sectionDao.hasStation(1L, 1L);
    }

    @DisplayName("구간 삭제 테스트")
    @Test
    void deleteStationInSection() {
        sectionDao.delete(1L, 1L);

        assertThat(sectionDao.findSectionsByLineId(1L)).hasSize(0);

    }
}
