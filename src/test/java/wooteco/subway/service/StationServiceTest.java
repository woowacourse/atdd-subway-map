package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Station;

@SpringBootTest
@Sql("/testSchema.sql")
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @DisplayName("역을 저장한다")
    @Test
    void 역_저장() {
        Station station = new Station("홍대입구역");

        Station savedStation = stationService.save(station);

        assertThat(savedStation.getName()).isEqualTo(station.getName());
    }

    @DisplayName("존재하는 이름의 역 저장 시 예외가 발생한다")
    @Test
    void 존재하는_이름의_역_저장_예외발생() {
        Station station = new Station("선릉역");
        Station duplicatedNameStation = new Station("선릉역");

        stationService.save(station);

        assertThatThrownBy(() -> stationService.save(duplicatedNameStation))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("이미 존재하는 역 이름");
    }

    @DisplayName("역을 조회한다")
    @Test
    void 역_조회() {
        Station savedStation = stationService.save(new Station("서울역"));

        Station foundStation = stationService.findById(savedStation.getId());

        assertThat(foundStation.getName()).isEqualTo(savedStation.getName());
    }

    @DisplayName("존재하지 않는 역 조회 시 예외가 발생한다")
    @Test
    void 존재하지_않는_역_조회_예외발생() {
        assertThatThrownBy(() -> stationService.findById(1L))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageContaining("존재하지 않는 역");
    }

    @DisplayName("모든 역을 조회한다")
    @Test
    void 모든_역_조회() {
        stationService.save(new Station("용산역"));
        stationService.save(new Station("잠실역"));
        stationService.save(new Station("강남역"));

        assertThat(stationService.findAll().size()).isEqualTo(3);
    }

    @DisplayName("역을 삭제한다")
    @Test
    void 역_삭제() {
        Station station = stationService.save(new Station("양재역"));

        stationService.deleteById(station.getId());

        assertThat(stationService.findAll().size()).isEqualTo(0);
    }
}