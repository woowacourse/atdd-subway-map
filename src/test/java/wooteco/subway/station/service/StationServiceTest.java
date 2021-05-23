package wooteco.subway.station.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.badrequest.DuplicatedNameException;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.Stations;
import wooteco.subway.station.repository.StationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
class StationServiceTest {
    private StationService stationService;

    @Autowired
    public StationServiceTest(StationService stationService) {
        this.stationService = stationService;
    }

    @DisplayName("지하철 역 저장한다.")
    @Test
    void save() {
        //given
        StationRequest 지하철_역_저장_요청 = new StationRequest("강남역");

        //when
        StationResponse 응답되는_역 = stationService.save(지하철_역_저장_요청);

        //then
        assertThat(응답되는_역.getName()).isEqualTo(지하철_역_저장_요청.getName());
    }

    @DisplayName("중복된 지하철 역 저장하면 예외 처리 한다.")
    void duplicateNameSave() {
        //given
        StationRequest 지하철_역_저장_요청 = new StationRequest("강남역");
        stationService.save(지하철_역_저장_요청);

        //when then
        assertThatThrownBy(() -> stationService.save(지하철_역_저장_요청)).isInstanceOf(DuplicatedNameException.class);
    }

    @DisplayName("지하철 역 전체 조회")
    @Test
    void findAll() {
        //given
        StationRequest 강남역_저장_요청 = new StationRequest("강남역");
        StationRequest 잠실역_저장_요청 = new StationRequest("잠실역");
        stationService.save(강남역_저장_요청);
        stationService.save(잠실역_저장_요청);

        //when
        List<StationResponse> 지하철역들 = stationService.findAll();

        //then
        assertThat(지하철역들).hasSize(2);
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void delete() {
        //given
        StationRequest 지하철_역_저장_요청 = new StationRequest("강남역");
        StationResponse 응답되는_역 = stationService.save(지하철_역_저장_요청);


        //when
        stationService.delete(응답되는_역.getId());
        List<StationResponse> 저장된_지하철_역들 = stationService.findAll();

        //then
        assertThat(저장된_지하철_역들).hasSize(0);
    }

    @DisplayName("순서가 정렬된 지하철 역 조회할 수 있다.")
    @Test
    void findSortStationsByIds() {
        //given
        Station 강남역 = 저장한_지하철역(new StationRequest("강남역"));
        Station 잠실역 = 저장한_지하철역(new StationRequest("잠실역"));
        Station 잠실나루역 = 저장한_지하철역(new StationRequest("잠실나루역"));

        //when
        Stations 조회한_역들 = stationService.findSortStationsByIds(Arrays.asList(강남역.getId(), 잠실나루역.getId(), 잠실역.getId()));

        //then
        assertThat(조회한_역들.toList()).containsExactly(강남역, 잠실나루역, 잠실역);
    }

    private Station 저장한_지하철역(StationRequest stationRequest) {
        StationResponse 저장된_지하철역_응답 = stationService.save(stationRequest);
        return new Station(저장된_지하철역_응답.getId(), 저장된_지하철역_응답.getName());
    }
}