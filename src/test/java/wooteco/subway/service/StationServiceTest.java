package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.utils.exception.NameDuplicatedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class StationServiceTest {

    @Autowired
    StationService stationService;

    @Autowired
    StationRepository stationRepository;

    @DisplayName("역 요청을 받아 저장한다.")
    @Test
    void save() {
        Station saveStation = stationService.save(new StationRequest("신림역"));
        Station findStation = stationRepository.findById(saveStation.getId());

        assertThat(saveStation.getId()).isEqualTo(findStation.getId());
    }

    @DisplayName("동일한 이름의 역이 있으면 에러를 발생한다.")
    @Test
    void saveDuplicateName() {
        stationService.save(new StationRequest("신림역"));
        assertThatThrownBy(
                () -> stationService.save(new StationRequest("신림역"))
        ).isInstanceOf(NameDuplicatedException.class);
    }

}
