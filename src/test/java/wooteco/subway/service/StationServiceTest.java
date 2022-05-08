package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Station;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Test
    @DisplayName("역 이름이 존재하지 않을 때 저장된다.")
    void save() {
        stationService.save(new Station("오리"));
        assertThatCode(() -> stationService.save(new Station("배카라")))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이미 존재하는 역 이름이 있을 때 예외가 발생한다.")
    void saveExceptionByDuplicatedName() {
        stationService.save(new Station("오리"));
        assertThatThrownBy(() -> stationService.save(new Station("오리")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 역 이름입니다.");
    }

    @Test
    @DisplayName("없는 id의 Station을 삭제할 수 없다.")
    void deleteByInvalidId() {
        Station station = stationService.save(new Station("오리"));
        Long stationId = station.getId() + 1;

        assertThatThrownBy(() -> stationService.delete(stationId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Station 입니다.");
    }

    @Test
    @DisplayName("이미 삭제한 id의 Station을 삭제할 수 없다.")
    void deleteByDuplicatedId() {
        Station station = stationService.save(new Station("오리"));
        Long stationId = station.getId();
        stationService.delete(stationId);

        assertThatThrownBy(() -> stationService.delete(stationId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("없는 Station 입니다.");
    }

    @Test
    @DisplayName("모든 Station을 조회할 수 있다.")
    void findAll() {
        stationService.save(new Station("오리"));
        stationService.save(new Station("배카라"));

        assertThat(stationService.findAll()).hasSize(2);
    }

}
