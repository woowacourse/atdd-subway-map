package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.dao.StationDao;

class StationServiceTest {

    private final StationService service = new StationService(new StationDao());

    @BeforeEach
    void initStore() {
        StationDao.removeAll();
    }

    @DisplayName("역 이름을 입력받아서 해당 이름을 가진 역을 등록한다.")
    @Test
    void register() {
        final Station created = service.register("선릉역");

        assertThat(created.getName()).isEqualTo("선릉역");
    }

    @DisplayName("이미 존재하는 역이름으로 등록하려할 시 예외가 발생한다.")
    @Test
    void registerDuplicateName() {
        service.register("선릉역");

        assertThatThrownBy(() -> service.register("선릉역"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 이미 존재하는 역 이름입니다.");
    }

    @DisplayName("등록된 모든 역 리스트를 조회한다.")
    @Test
    void searchAll() {
        service.register("선릉역");
        service.register("강남역");
        service.register("잠실역");

        List<Station> stations = service.searchAll();
        List<String> names = stations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        assertThat(names).isEqualTo(List.of("선릉역", "강남역", "잠실역"));
    }

    @DisplayName("id 로 지하철역을 삭제한다.")
    @Test
    void removeById() {
        Station station = service.register("신림역");

        service.remove(station.getId());

        assertThatThrownBy(() -> service.searchById(station.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("[ERROR] 지하철역이 존재하지 않습니다");
    }
}