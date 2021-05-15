package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Deque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.InvalidDistanceException;
import wooteco.subway.exception.NotFoundException;
import wooteco.subway.service.dto.DeleteStationDto;
import wooteco.subway.service.dto.SectionServiceDto;

@Sql("classpath:initializeData.sql")
@SpringBootTest
public class SectionServiceTest {

    private Long lineId;
    private Long stationSinSeolId;
    private Long stationDongMyoId;
    private Long stationDongDaeMoonId;
    private int distance;

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        lineId = 1L;
        stationSinSeolId = 1L;
        stationDongMyoId = 2L;
        stationDongDaeMoonId = 3L;
        distance = 10;

        SectionServiceDto sinSeolAndDongMyoDto = new SectionServiceDto(lineId, stationSinSeolId,
            stationDongMyoId, distance);
        SectionServiceDto dongMyoAndDongDaeMoonDto = new SectionServiceDto(lineId, stationDongMyoId,
            stationDongDaeMoonId, distance);
        sectionService.saveByLineCreate(sinSeolAndDongMyoDto);
        sectionService.save(dongMyoAndDongDaeMoonDto);
    }

    @Test
    @DisplayName("Section 추가")
    void createSectionWithLineAndStations() {
        // given
        Long sectionId = 3L;
        Long downStationId = 4L;
        Long upStationId = 3L;
        int distance = 10;
        String name = "회기역";
        stationDao.save(new Station(name));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, upStationId,
            downStationId, distance);
        SectionServiceDto savedDongDaeMoonAndHaegiDto = sectionService.save(dongDaeMoonAndHaegiDto);

        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getId()).isEqualTo(sectionId);
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(upStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(downStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("역 사이에 추가")
    void createSectionBetweenSections() {
        // given
        Station 회기역 = stationDao.save(new Station("회기역"));
        Long targetUpStationId = 2L;
        int distance = 3;
        Long targetDownStationId = 회기역.getId();

        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, targetUpStationId,
            targetDownStationId, distance);

        // when
        SectionServiceDto savedDongDaeMoonAndHaegiDto = sectionService.save(dongDaeMoonAndHaegiDto);
        Section changedSection = sectionDao.findByLineIdAndDownStationId(lineId, targetDownStationId).get();

        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(targetUpStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(targetDownStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(distance);

        assertThat(changedSection.getDistance()).isEqualTo(distance);
        assertThat(changedSection.getUpStationId()).isEqualTo(targetUpStationId);
    }

    @Test
    @DisplayName("역 사이에 먼 거리 추가")
    void createSectionBetweenSectionsWithExcessDistance() {
        // given
        Long targetUpStationId = 2L;
        Long targetDownStationId = 9L;
        int distance = 15;
        String name = "회기역";
        stationDao.save(new Station(name));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto =
            new SectionServiceDto(lineId, targetUpStationId, targetDownStationId, distance);

        // then
        assertThatThrownBy(() -> sectionService.save(dongDaeMoonAndHaegiDto))
            .isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    @DisplayName("지하철 역이 3개 이상일 때 노선의 중간역 삭제")
    void deleteSection() {
        // given
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationDongMyoId);

        // when
        sectionService.delete(deleteStationDto);
        Section section = sectionDao.findByLineIdAndUpStationId(lineId, stationSinSeolId)
            .orElseThrow(NotFoundException::new);

        // then
        assertThat(section.getUpStationId()).isEqualTo(stationSinSeolId);
        assertThat(section.getDownStationId()).isEqualTo(stationDongDaeMoonId);
        assertThat(section.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("지하철 역이 3개 이상일 때 노선 끝에 존재하는 역(구간) 삭제")
    void deleteSectionAtEnd() {
        // given
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationDongDaeMoonId);

        // when
        Sections beforeSections = new Sections(sectionDao.findAllByLineId(lineId));
        Deque<Long> beforeStationIds = beforeSections.sortedStationIds();
        sectionService.delete(deleteStationDto);
        Sections afterSections = new Sections(sectionDao.findAllByLineId(lineId));
        Deque<Long> afterStationIds = afterSections.sortedStationIds();

        // then
        assertThat(beforeStationIds.peekFirst()).isEqualTo(stationSinSeolId);
        assertThat(beforeStationIds.peekLast()).isEqualTo(stationDongDaeMoonId);
        assertThat(afterStationIds.peekFirst()).isEqualTo(stationSinSeolId);
        assertThat(afterStationIds.peekLast()).isEqualTo(stationDongMyoId);
    }

    @Test
    @DisplayName("지하철 역이 2개만 있을 때(구간이 1개일 때)의 삭제")
    void deleteSectionWithTwoStations() {
        // given
        DeleteStationDto preparedDeleteStationDto = new DeleteStationDto(lineId, stationDongMyoId);
        DeleteStationDto targetDeleteStationDto = new DeleteStationDto(lineId, stationSinSeolId);

        // when
        sectionService.delete(preparedDeleteStationDto);

        // then
        assertThatThrownBy(() -> sectionService.delete(targetDeleteStationDto))
            .isInstanceOf(IllegalStateException.class);
    }
}
