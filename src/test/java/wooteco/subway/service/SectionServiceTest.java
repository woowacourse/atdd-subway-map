package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.InmemoryLineDao;
import wooteco.subway.dao.InmemorySectionDao;
import wooteco.subway.dao.InmemoryStationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionSaveRequest;

class SectionServiceTest {

    private final InmemoryLineDao lineDao = InmemoryLineDao.getInstance();
    private final InmemorySectionDao sectionDao = InmemorySectionDao.getInstance();
    private final InmemoryStationDao stationDao = InmemoryStationDao.getInstance();

    private final SectionService sectionService = new SectionService(lineDao, sectionDao, stationDao);

    @AfterEach
    void afterEach() {
        lineDao.clear();
        sectionDao.clear();
        stationDao.clear();
    }

    @Test
    @DisplayName("Section을 추가할 수 있다.")
    void save() {
        // given
        long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        long stationId1 = stationDao.save(new Station("오리")).getId();
        long stationId2 = stationDao.save(new Station("배카라")).getId();
        long stationId3 = stationDao.save(new Station("오카라")).getId();
        sectionDao.save(new Section(lineId, new Station(stationId1, "오리"), new Station(stationId3, "오카라"), 10));

        // when
        int result = sectionService.save(lineId, new SectionSaveRequest(stationId1, stationId2, 3));

        // then
        assertThat(result).isEqualTo(result);
    }

    @Test
    @DisplayName("Station을 받아 구간을 삭제할 수 있다.")
    void delete() {
        // given
        long lineId = lineDao.save(new Line("신분당선", "bg-red-600"));
        long stationId1 = stationDao.save(new Station("오리")).getId();
        long stationId2 = stationDao.save(new Station("배카라")).getId();
        long stationId3 = stationDao.save(new Station("오카라")).getId();
        sectionDao.save(new Section(lineId, new Station(stationId1, "오리"), new Station(stationId2, "배카라"), 10));
        sectionDao.save(new Section(lineId, new Station(stationId2, "배카라"), new Station(stationId3, "오카라"), 10));

        // when
        sectionService.delete(lineId, stationId2);

        // then
        assertThat(sectionDao.findAllByLineId(lineId)).hasSize(1);
    }
}
