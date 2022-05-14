package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.SectionEntity;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.SectionDto;

class SectionServiceTest {

    private final Long line2Id = 1L;
    private final Long 선릉역Id = 1L;
    private final Long 강남역Id = 2L;
    private final Long 서초역Id = 3L;
    private final Station 선릉역 = new Station(선릉역Id, "선릉");
    private final Station 강남역 = new Station(강남역Id, "강남");
    private final Station 서초역 = new Station(서초역Id, "서초");
    private SectionDao sectionDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionDao = new FakeSectionDao();
        StationDao stationDao = new FakeStationDao();

        stationDao.save(선릉역);
        stationDao.save(강남역);
        stationDao.save(서초역);

        sectionService = new SectionService(sectionDao, new StationService(stationDao));
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void createSection() {
        //given
        sectionService.createSection(new SectionDto(line2Id, 선릉역Id, 강남역Id, 10));

        //when
        List<SectionEntity> sectionEntities = sectionDao.findByLineId(line2Id);

        //then
        assertThat(sectionEntities).contains(new SectionEntity(1L, line2Id, 선릉역Id, 강남역Id, 10));
    }

    @Test
    @DisplayName("노선에 새 구간을 추가한다.")
    void saveSection() {
        //given
        sectionService.createSection(new SectionDto(line2Id, 선릉역Id, 강남역Id, 5));

        //when
        sectionService.addSectionInLine(new SectionDto(line2Id, 강남역Id, 서초역Id, 5));

        //then
        List<Station> actual = sectionService.findStationsByLineId(line2Id);
        List<Station> expected = List.of(선릉역, 강남역, 서초역);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 노선에 해당하는 역 목록을 조회한다.")
    void findStationsByLineId() {
        //given
        sectionService.createSection(new SectionDto(line2Id, 선릉역Id, 강남역Id, 5));
        sectionService.createSection(new SectionDto(line2Id, 강남역Id, 서초역Id, 5));

        //when
        List<Station> actual = sectionService.findStationsByLineId(line2Id);
        List<Station> expected = List.of(선릉역, 강남역, 서초역);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 노선에 있는 역을 삭제한다.")
    void deleteStation() {
        //given
        sectionService.createSection(new SectionDto(line2Id, 선릉역Id, 강남역Id, 5));
        sectionService.createSection(new SectionDto(line2Id, 강남역Id, 서초역Id, 5));

        //when
        sectionService.deleteStation(line2Id, 선릉역Id);
        List<Station> actual = sectionService.findStationsByLineId(line2Id);
        List<Station> expected = List.of(강남역, 서초역);

        //then
        assertThat(actual).isEqualTo(expected);
    }
}
