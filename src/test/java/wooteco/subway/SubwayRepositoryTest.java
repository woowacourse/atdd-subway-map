package wooteco.subway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.SubwayRepository;
import wooteco.subway.repository.dao.LineDao;
import wooteco.subway.repository.dao.SectionDao;
import wooteco.subway.repository.dao.StationDao;

@JdbcTest
public class SubwayRepositoryTest {
    StationDao stationDao;
    LineDao lineDao;
    SectionDao sectionDao;
    SubwayRepository subwayRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        sectionDao = new SectionDao(jdbcTemplate);
        subwayRepository = new SubwayRepository(stationDao, lineDao, sectionDao);
    }

    @Test
    @DisplayName("노선 id에 포함된 역들이 상행->하행 순으로 정렬되어 반환")
    void findStationsByLineId() {
        //given
        Line 크로플선 = lineDao.insert(new Line("red", "2호선"));
        Station 신림역 = stationDao.insert("신림역");
        Station 봉천역 = stationDao.insert("봉천역");
        Station 사당역 = stationDao.insert("사당역");
        Station 강남역 = stationDao.insert("강남역");
        Station 잠실역 = stationDao.insert("잠실역");

        sectionDao.insert(new Section(크로플선.getId(), 봉천역.getId(), 사당역.getId(), 5));
        sectionDao.insert(new Section(크로플선.getId(), 신림역.getId(), 봉천역.getId(), 5));
        sectionDao.insert(new Section(크로플선.getId(), 강남역.getId(), 잠실역.getId(), 5));
        sectionDao.insert(new Section(크로플선.getId(), 사당역.getId(), 강남역.getId(), 5));

        subwayRepository.findStationsByLineId(크로플선.getId());
    }
}
