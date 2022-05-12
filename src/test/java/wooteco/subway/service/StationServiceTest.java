package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.domain.repository.SectionRepositoryImpl;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.domain.repository.StationRepositoryImpl;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.utils.exception.DuplicatedException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class StationServiceTest {

    @Autowired
    private DataSource dataSource;

    private StationService stationService;
    private StationRepository stationRepository;

    @BeforeEach
    void setUp() {
        stationRepository = new StationRepositoryImpl(dataSource);
        SectionRepository sectionRepository = new SectionRepositoryImpl(dataSource);
        stationService = new StationService(stationRepository, sectionRepository);
    }

    @DisplayName("역 요청을 받아 저장한다.")
    @Test
    void save() {
        StationResponse stationResponse = stationService.save(new StationRequest("신림역"));
        Station findStation = stationRepository.findById(stationResponse.getId()).get();

        assertThat(stationResponse.getId()).isEqualTo(findStation.getId());
    }

    @DisplayName("동일한 이름의 역이 있으면 에러를 발생한다.")
    @Test
    void saveDuplicateName() {
        stationRepository.save(new Station("신림역"));
        assertThatThrownBy(
                () -> stationService.save(new StationRequest("신림역"))
        ).isInstanceOf(DuplicatedException.class);
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void showStations() {
        List<StationResponse> stationResponses = stationService.showStations();
        assertThat(stationResponses).hasSize(3);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = stationRepository.save(new Station("신림역"));
        stationService.deleteStation(station.getId());
        assertThat(stationRepository.findById(station.getId())).isEmpty();
    }

}
