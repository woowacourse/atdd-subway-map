package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.SectionEntity;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
class LineServiceTest {

    @Autowired
    private LineService lineService;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("TRUNCATE table line");
        jdbcTemplate.update("TRUNCATE table station");
        jdbcTemplate.update("TRUNCATE table section");
    }

    @DisplayName("없는 역으로 구간 생성시 에러가 발생한다.")
    @Test
    void create_with_none_station_false() {
        // given
        Station 미르역 = stationDao.save(new Station("미르역"));
        Station 없는역 = new Station(100L, "없는역");
        Line 우테코노선 = lineDao.save(new Line("우테코노선", "노랑"));

        // when, then
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> lineService.createSection(우테코노선.getId(),
                        new SectionRequest(미르역.getId(), 없는역.getId(), 100)));
    }

    @DisplayName("맨 앞에 구간을 추가한다.")
    @Test
    void create_front_add_section() {
        // given
        Station 미르역 = stationDao.save(new Station("미르역"));
        Station 수달역 = stationDao.save(new Station("수달역"));
        Station 호호역 = stationDao.save(new Station("호호역"));
        Line 우테코노선 = new Line("우테코노선", "노랑");

        LineResponse lineResponse = lineService.create(
                new LineRequest(우테코노선.getName(), 우테코노선.getColor(), 미르역.getId(), 수달역.getId(), 100));
        // when
        lineService.createSection(lineResponse.getId(),
                new SectionRequest(호호역.getId(), 미르역.getId(), 100));

        // then
        assertThat(lineService.showById(lineResponse.getId()).getStations().size()).isEqualTo(3);
    }

    @DisplayName("맨 뒤에 구간을 추가한다.")
    @Test
    void create_back_add_section() {
        // given
        Station 미르역 = stationDao.save(new Station("미르역"));
        Station 수달역 = stationDao.save(new Station("수달역"));
        Station 호호역 = stationDao.save(new Station("호호역"));
        Line 우테코노선 = new Line("우테코노선", "노랑");

        LineRequest request = new LineRequest(우테코노선.getName(), 우테코노선.getColor(), 미르역.getId(), 수달역.getId(), 100);

        LineResponse lineResponse = lineService.create(request);
        // when
        lineService.createSection(lineResponse.getId(),
                new SectionRequest(수달역.getId(), 호호역.getId(), 100));

        // then
        assertThat(lineService.showById(lineResponse.getId()).getStations().size()).isEqualTo(3);
    }


    @Test
    @DisplayName("구간 사이에 역을 추가한다.")
    void addSectionInSection() {
        Station 미르역 = stationDao.save(new Station("미르역"));
        Station 수달역 = stationDao.save(new Station("수달역"));
        Station 호호역 = stationDao.save(new Station("호호역"));
        Line 우테코노선 = new Line("우테코노선", "노랑");

        LineRequest request = new LineRequest(우테코노선.getName(), 우테코노선.getColor(), 미르역.getId(), 수달역.getId(), 100);

        LineResponse lineResponse = lineService.create(request);
        // when
        lineService.createSection(lineResponse.getId(),
                new SectionRequest(미르역.getId(), 호호역.getId(), 40));

        // then
        List<SectionEntity> result = sectionDao.findByLineId(lineResponse.getId());
        assertThat(result.size()).isEqualTo(2);
        // assertThat(result)
    }


    private SectionEntity findSectionEntity(Station startStation, List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
                .filter(entity -> entity.getUpStationId().equals(startStation.getId()))
                .findFirst()
                .get();
    }

}
