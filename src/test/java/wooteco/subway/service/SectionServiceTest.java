package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.section.Section;
import wooteco.subway.dto.request.SectionRequest;

@JdbcTest
class SectionServiceTest {

    @Autowired
    private DataSource dataSource;

    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        this.sectionDao = new JdbcSectionDao(dataSource);
        this.stationDao = new JdbcStationDao(dataSource);
        this.lineDao = new JdbcLineDao(dataSource);
        this.sectionService = new SectionService(sectionDao, stationDao, lineDao);
    }

    @DisplayName("특정 호선에 속하는 상/하행 종점역을 조회한다.")
    @Test
    void findSectionStationsByLineId() {
        final Long lineId = 1L;
        final Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        final Station 신분당_1역 = stationDao.save(new Station("신분당_1역"));
        final Station 신분당_3역 = stationDao.save(new Station("신분당_3역"));
        sectionDao.save(new Section(lineId, 신분당_1역.getId(), 신분당_3역.getId(), 10));

        final List<Station> stations = sectionService.findSectionStationsByLineId(lineId);

        assertThat(stations).hasSize(2);
    }

    @DisplayName("기본 구간에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        final Long lineId = 1L;
        final Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        final Station 신분당_1역 = stationDao.save(new Station("신분당_1역"));
        final Station 신분당_2역 = stationDao.save(new Station("신분당_2역"));
        final Station 신분당_3역 = stationDao.save(new Station("신분당_3역"));
        sectionDao.save(new Section(lineId, 신분당_1역.getId(), 신분당_3역.getId(), 10));

        final SectionRequest sectionRequest = new SectionRequest(신분당_1역.getId(), 신분당_2역.getId(), 3);

        assertDoesNotThrow(() -> sectionService.addSection(lineId, sectionRequest));
    }

    @DisplayName("기존 구간에 일부 구간을 삭제한다.")
    @Test
    void deleteSection() {
        final Line 분당선 = lineDao.save(new Line("분당선", "red"));
        final Station 분당_1역 = stationDao.save(new Station("분당_1역"));
        final Station 분당_2역 = stationDao.save(new Station("분당_2역"));
        final Station 분당_3역 = stationDao.save(new Station("분당_3역"));
        sectionDao.save(new Section(분당선.getId(), 분당_1역.getId(), 분당_2역.getId(), 10));
        sectionDao.save(new Section(분당선.getId(), 분당_2역.getId(), 분당_3역.getId(), 10));

        Assertions.assertDoesNotThrow(() -> sectionService.deleteSection(분당선.getId(), 분당_2역.getId()));
    }
}
