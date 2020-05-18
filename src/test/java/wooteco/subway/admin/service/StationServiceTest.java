package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.StationCreateRequest;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StationServiceTest {
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineRepository lineRepository;

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(lineRepository, stationRepository);
    }

    @Test
    void addStation() {
        StationCreateRequest stationCreateRequest = new StationCreateRequest("의정부역");
        when(stationRepository.findByName("의정부역")).thenReturn(Optional.of(new Station(1L, "의정부역")));
        assertThatThrownBy(() -> stationService.addStation(stationCreateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 이름이 존재합니다.");
    }

    @Test
    void deleteByIdWithException() {
        when(lineRepository.countByStationId(anyLong())).thenReturn(5);
        assertThatThrownBy(() -> stationService.deleteById(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역이 노선에 존재합니다. 해당되는 노선에서 역을 모두 지우세요.");
    }
}