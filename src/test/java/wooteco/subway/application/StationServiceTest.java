package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.exception.DuplicateStationNameException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.exception.BlankArgumentException;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.repository.StationRepository;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationRepository stationRepository;

    @DisplayName("지하철역 저장")
    @Test
    void saveByName() {
        String stationName = "something";
        Station station = stationService.save(new StationRequest(stationName));
        assertThat(stationRepository.findById(station.getId())).isNotEmpty();
    }

    @DisplayName("중복된 지하철역 저장")
    @Test
    void saveByDuplicateName() {
        String stationName = "something";
        stationService.save(new StationRequest(stationName));

        assertThatThrownBy(() -> stationService.save(new StationRequest(stationName)))
            .isInstanceOf(DuplicateStationNameException.class);
    }

    @DisplayName("지하철 역 이름에 빈 문자열을 저장할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void saveByEmptyName(String stationName) {
        assertThatThrownBy(() -> stationService.save(new StationRequest(stationName)))
            .isInstanceOf(BlankArgumentException.class);
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void deleteById() {
        Station station = stationService.save(new StationRequest("강남역"));

        stationService.deleteById(station.getId());

        assertThat(stationRepository.existByName("강남역")).isFalse();
    }

    @DisplayName("존재하지 않는 지하철 역 삭제")
    @Test
    void deleteNotExistStation() {
        assertThatThrownBy(() -> stationService.deleteById(50L))
            .isInstanceOf(NotFoundStationException.class);
    }
}
