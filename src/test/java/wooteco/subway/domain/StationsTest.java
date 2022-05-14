package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.ClientException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationsTest {

    @Test
    @DisplayName("중복된 지하철 역 저장시 예외")
    void duplicateName() {
        Stations stations = new Stations(new ArrayList<>(List.of(new Station("선릉역"), new Station("강남역"))));

        assertThatThrownBy(() -> stations.add(new Station("강남역")))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철역입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 지하철 역 검색시 예외")
    void canDelete() {
        Stations stations = new Stations(new ArrayList<>(List.of(new Station(1L,"선릉역"),
                new Station(2L, "강남역"))));

        assertThatThrownBy(() -> stations.validateExist(3L))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("존재하지 않는 역입니다.");
    }
}
