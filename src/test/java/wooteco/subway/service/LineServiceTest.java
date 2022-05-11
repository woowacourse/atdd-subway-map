package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @DisplayName("중복되는 노선 이름이 없을 때 성공적으로 저장되는지 테스트")
    @Test
    void save() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "yellow", station1.getId(), station2.getId(), 10L));
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }

    @DisplayName("전체 노선을 성공적으로 조회할 수 있는지 테스트")
    @Test
    void findAll() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        LineResponse lineResponse1 = lineService.save(new LineRequest("신분당선", "yellow", station1.getId(), station2.getId(), 10L));
        LineResponse lineResponse2 = lineService.save(new LineRequest("2호선", "yellow", station1.getId(), station2.getId(), 10L));
        List<LineResponse> result = lineService.findAll();
        assertAll(() -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getStations().size()).isEqualTo(2),
                () -> assertThat(result.get(1).getStations().size()).isEqualTo(2));
    }

    @DisplayName("중복되는 노선 이름이 있을 때 에러가 발생하는지 테스트")
    @Test
    void save_duplicate() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "green", station1.getId(), station2.getId(), 10L));
        assertThatThrownBy(() -> lineService.save(new LineRequest("신분당선", "green", station1.getId(), station2.getId(), 10L)))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("유효한 id를 가진 노선을 가져오는지 테스트")
    @Test
    void findById() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "green", station1.getId(), station2.getId(), 10L));
        assertThat(lineService.findById(lineResponse.getId()).getName()).isEqualTo("신분당선");
    }

    @DisplayName("없는 id를 가진 노선을 조회할 때 예외가 발생하는지 테스트")
    @Test
    void findById_no_eixst_id() {
        assertThatThrownBy(() -> lineService.findById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하지 않는 id의 이름을 바꿀때 예외가 발생하는지 테스트")
    @Test
    void change_name_no_exist_id() {
        assertThatThrownBy(() -> lineService.changeLineName(-1L, "test"))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하지 않는 id로 노선을 삭제할 때 예외가 발생하는지 테스트")
    @Test
    void delete_no_exist_id() {
        assertThatThrownBy(() -> lineService.deleteById(-1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("존재하는 id로 노선을 삭제 후 삭제한 노선을 찾을 때 예외가 발생하는지 테스트")
    @Test
    void delete() {
        Station station1 = stationDao.save(new Station("강남역"));
        Station station2 = stationDao.save(new Station("선릉역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "yellow", station1.getId(), station2.getId(), 10L));
        lineService.deleteById(lineResponse.getId());
        assertThatThrownBy(() -> lineService.findById(lineResponse.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
