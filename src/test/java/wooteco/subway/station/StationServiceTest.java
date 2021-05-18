package wooteco.subway.station;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.exception.StationException;
import wooteco.subway.station.service.StationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class StationServiceTest {

    private final String savedName = "코기역";
    @Autowired
    private StationService stationService;
    @MockBean
    private SectionDao sectionDao;
    private StationResponse savedStation;

    @BeforeEach
    private void init() {
        savedStation = stationService.save(new StationRequest(savedName));
    }

    @DisplayName("역을 추가한다.")
    @Test
    public void insertStation() {
        stationService.save(new StationRequest("validName"));
    }

    @DisplayName("중복 이름의 역을 추가할 수 없다.")
    @Test
    public void insertStationWithDuplicatedName() {
        final String duplicatedName = savedName;
        assertThatThrownBy(() -> {
            stationService.save(new StationRequest(duplicatedName));
        }).isInstanceOf(StationException.class);
    }

    @DisplayName("역 제거")
    @Test
    public void deleteStation() {
        final int preSize = stationService.findAll().size();
        stationService.delete(savedStation.getId());
        final int postSize = stationService.findAll().size();

        assertThat(preSize - 1).isEqualTo(postSize);
    }

    @DisplayName("존재하지 않는 역은 제거할 수 없다.")
    @Test
    public void deleteNonExistentStation() {
        assertThatThrownBy(() -> {
            stationService.delete(Long.MAX_VALUE);
        }).isInstanceOf(StationException.class);
    }

    @DisplayName("구간에 등록되어 있는 역을 제거할 수 없음")
    @Test
    public void deleteStationInSection() {
        Mockito.when(sectionDao.isExistingStation(savedStation.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> {
            stationService.delete(savedStation.getId());
        }).isInstanceOf(StationException.class);
    }
}
