package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;
import wooteco.subway.service.dto.StationRequest;
import wooteco.subway.service.dto.StationResponse;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("역을 생성한다.")
    void create() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        StationResponse response = stationService.create(request);

        // then
        assertThat(response.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("모든 역을 조회한다.")
    void findAll() {
        // given
        stationDao.save(new StationEntity(null, "노원역"));
        stationDao.save(new StationEntity(null, "왕십리역"));

        // when
        List<StationResponse> stationResponses = stationService.findAll();

        // then
        assertThat(stationResponses).hasSize(2);
    }

    @Test
    @DisplayName("id에 해당하는 역을 삭제한다.")
    void delete() {
        // given
        StationEntity savedEntity = stationDao.save(new StationEntity(null, "마들역"));

        // when
        stationService.delete(savedEntity.getId());

        // then
        List<StationEntity> remainEntities = stationDao.findAll();
        assertThat(remainEntities).hasSize(0);
    }
}
