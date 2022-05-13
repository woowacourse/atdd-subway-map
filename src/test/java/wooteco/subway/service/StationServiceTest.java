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
import wooteco.subway.utils.exception.NotFoundException;

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

    @DisplayName("이미 존재하는 역을 한번 더 저장할 때 예외가 발생한다.")
    @Test
    void saveFailure() {
        stationService.save(new StationRequest("신림역"));

        assertThatThrownBy(
                () -> stationService.save(new StationRequest("신림역"))
        ).isExactlyInstanceOf(DuplicatedException.class).hasMessage("[ERROR] 이미 존재하는 역의 이름입니다.");
    }

    @DisplayName("동일한 이름의 역이 있으면 예외가 발생한다.")
    @Test
    void saveDuplicateName() {
        stationRepository.save(new Station("신림역"));
        assertThatThrownBy(
                () -> stationService.save(new StationRequest("신림역"))
        ).isExactlyInstanceOf(DuplicatedException.class).hasMessage("[ERROR] 이미 존재하는 역의 이름입니다.");
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void showStations() {
        int 전체_역의_개수 = 3;
        List<StationResponse> stationResponses = stationService.showStations();
        assertThat(stationResponses).hasSize(전체_역의_개수);
    }

    @DisplayName("역을 삭제한다.")
    @Test
    void deleteStation() {
        Station station = stationRepository.save(new Station("신림역"));
        stationService.deleteById(station.getId());
        assertThat(stationRepository.findById(station.getId())).isEmpty();
    }

    @DisplayName("없는 역을 삭제하려고 하면 예외가 발생한다.")
    @Test
    void deleteStationFailure() {

        long 없는_역_ID = 999L;
        assertThatThrownBy(
                () ->        stationService.deleteById(없는_역_ID)
        ).isInstanceOf(NotFoundException.class).hasMessage("[ERROR] 식별자에 해당하는 역을 찾을수 없습니다.");
    }

}
