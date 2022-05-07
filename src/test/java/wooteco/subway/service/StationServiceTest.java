package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@SpringBootTest
public class StationServiceTest {
    private final StationService stationService;

    @Autowired
    private StationServiceTest(StationDao stationDao) {
        this.stationService = new StationService(stationDao);
    }

    @DisplayName("중복되는 역 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save() {
        StationResponse stationResponse = stationService.save(new StationRequest("대흥역"));
        assertThat(stationResponse.getName()).isEqualTo("대흥역");
    }

    @DisplayName("중복되는 역 이름이 있을 때 에러가 발생하는지 테스트")
    @Test
    void save_duplicate() {
        StationResponse stationResponse = stationService.save(new StationRequest("화곡역"));
        assertThatThrownBy(() -> stationService.save(new StationRequest("화곡역")))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("존재하지 않는 id로 역을 삭제할 때 예외가 발생하는지 테스트")
    @Test
    void delete_no_exist_id() {
        assertThatThrownBy(() -> stationService.deleteById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
