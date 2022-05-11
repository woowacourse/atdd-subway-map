package wooteco.subway.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.entity.StationEntity;

@SpringBootTest
@Transactional
@Sql("/insert_line.sql")
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationDao stationDao;

    private Long stationAId;
    private Long stationBId;
    private Long stationCId;
    private Long stationDId;

    @BeforeEach
    void setUp() {
        stationAId = stationDao.save(new StationEntity.Builder("A").build()).getId();
        stationBId = stationDao.save(new StationEntity.Builder("B").build()).getId();
        stationCId = stationDao.save(new StationEntity.Builder("C").build()).getId();
        stationDId = stationDao.save(new StationEntity.Builder("D").build()).getId();
    }

    @DisplayName("구간이 아무 것도 없을 때 구간을 추가한다.")
    @Test
    void createSectionWithNone() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);

        sectionService.createSection(1L, sectionRequest);

        List<String> orderedStationNames = sectionService.getOrderedStations(1L).stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(orderedStationNames).containsExactly("A", "B");
    }

    @DisplayName("구간이 이미 있을 때 상행 종점에 구간을 추가한다.")
    @Test
    void createSectionInLastUpStation() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);
        sectionService.createSection(1L, sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(stationCId, stationAId, 10);
        sectionService.createSection(1L, sectionRequest2);

        List<String> orderedStationNames = sectionService.getOrderedStations(1L).stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(orderedStationNames).containsExactly("C", "A", "B");
    }

    @DisplayName("구간이 이미 있을 때 하행 종점에 구간을 추가한다.")
    @Test
    void createSectionInLastDownStation() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);
        sectionService.createSection(1L, sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(stationBId, stationCId, 10);
        sectionService.createSection(1L, sectionRequest2);

        List<String> orderedStationNames = sectionService.getOrderedStations(1L).stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(orderedStationNames).containsExactly("A", "B", "C");
    }

    @DisplayName("구간이 이미 있을 때 중간에 구간을 추가한다.")
    @Test
    void createSectionInMiddle() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);
        sectionService.createSection(1L, sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(stationAId, stationCId, 3);
        sectionService.createSection(1L, sectionRequest2);

        SectionRequest sectionRequest3 = new SectionRequest(stationDId, stationBId, 6);
        sectionService.createSection(1L, sectionRequest3);

        List<String> orderedStationNames = sectionService.getOrderedStations(1L).stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(orderedStationNames).containsExactly("A", "C", "D", "B");
    }

    @DisplayName("구간이 이미 있을 때 중간에 거리가 나누어 져서 새로 추가할 때 거리가 너무 길면 예외가 발생한다.")
    @Test
    void createSectionSplitDistance() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);
        sectionService.createSection(1L, sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(stationAId, stationCId, 3);
        sectionService.createSection(1L, sectionRequest2);

        SectionRequest sectionRequest3 = new SectionRequest(stationDId, stationBId, 7);

        assertThatThrownBy(() -> sectionService.createSection(1L, sectionRequest3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간 사이의 거리가 너무 멉니다.");
    }

    @DisplayName("구간이 이미 있을 때 해당 구간을 추가하려고 하면 예외가 발생한다.")
    @Test
    void createSectionWithAlreadyExistingSection() {
        SectionRequest sectionRequest = new SectionRequest(stationAId, stationBId, 10);
        sectionService.createSection(1L, sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(stationBId, stationCId, 3);
        sectionService.createSection(1L, sectionRequest2);

        SectionRequest sectionRequest3 = new SectionRequest(stationAId, stationCId, 3);

        assertThatThrownBy(() -> sectionService.createSection(1L, sectionRequest3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("입력한 상행 하행 구간이 이미 연결되어 있는 구간입니다.");
    }
}
