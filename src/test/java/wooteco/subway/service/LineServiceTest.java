package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
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
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    public void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from line");
        jdbcTemplate.update("delete from station");
    }

    @Test
    @DisplayName("구간 사이에 역을 추가한다. 강남-광교 -> 강남-양재-광교")
    void addSection() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(신분당선.getId());
        SectionEntity 구간1 = findSectionEntity(강남, sectionEntities);
        SectionEntity 구간2 = findSectionEntity(양재, sectionEntities);
        assertThat(구간1.getDownStationId()).isEqualTo(양재.getId());
        assertThat(구간1.getDistance()).isEqualTo(4);
        assertThat(구간2.getDownStationId()).isEqualTo(광교.getId());
        assertThat(구간2.getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("상행 종점을 변경한다. 양재-광교 -> 강남-양재-광교")
    void addUpStation() {
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Station 강남 = stationDao.save(new Station("강남"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 양재, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 5);
        lineService.createSection(신분당선.getId(), sectionRequest);

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(신분당선.getId());
        SectionEntity 구간1 = findSectionEntity(강남, sectionEntities);
        SectionEntity 구간2 = findSectionEntity(양재, sectionEntities);
        assertThat(구간1.getDownStationId()).isEqualTo(양재.getId());
        assertThat(구간1.getDistance()).isEqualTo(5);
        assertThat(구간2.getDownStationId()).isEqualTo(광교.getId());
        assertThat(구간2.getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("하행 종점을 변경한다. 강남-양재 -> 강남-양재-광교")
    void addDownStation() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 양재, 10));

        SectionRequest sectionRequest = new SectionRequest(양재.getId(), 광교.getId(), 5);
        lineService.createSection(신분당선.getId(), sectionRequest);

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(신분당선.getId());
        SectionEntity 구간1 = findSectionEntity(강남, sectionEntities);
        SectionEntity 구간2 = findSectionEntity(양재, sectionEntities);
        assertThat(구간1.getDownStationId()).isEqualTo(양재.getId());
        assertThat(구간1.getDistance()).isEqualTo(10);
        assertThat(구간2.getDownStationId()).isEqualTo(광교.getId());
        assertThat(구간2.getDistance()).isEqualTo(5);
    }

    @Test
    @DisplayName("중간 역을 삭제한다. 강남-양재-광교 -> 강남-광교")
    void deleteSection() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        lineService.delete(신분당선.getId(), 양재.getId());

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(신분당선.getId());
        assertThat(sectionEntities).hasSize(1);
        assertThat(sectionEntities.get(0).getDistance()).isEqualTo(10);
        assertThat(sectionEntities).extracting("upStationId").isEqualTo(List.of(강남.getId()));
        assertThat(sectionEntities).extracting("downStationId").isEqualTo(List.of(광교.getId()));
    }

    @Test
    @DisplayName("상행 종점을 삭제한다. 강남-양재-광교 -> 양재-광교")
    void deleteUpSection() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));
        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 4);
        lineService.createSection(신분당선.getId(), sectionRequest);

        lineService.delete(신분당선.getId(), 강남.getId());

        List<SectionEntity> sectionEntities = sectionDao.findByLineId(신분당선.getId());
        assertThat(sectionEntities).hasSize(1);
        assertThat(sectionEntities.get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("라인에 없는 역을 구간으로 추가할 경우 예외를 발생한다.")
    void addSectionNotFindStation() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Station 창동 = stationDao.save(new Station("창동"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(양재.getId(), 창동.getId(), 4);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("생성할 수 없는 구간입니다.");
    }

    @Test
    @DisplayName("라인에 둘다 존재하는 역을 구간으로 추가할 경우 예외를 발생한다.")
    void addSectionDuplicateStation() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 광교.getId(), 4);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존에 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("기존 구간보다 긴 거리의 구간으로 추가시 예외를 발생한다.")
    void addSectionOverDistance() {
        Station 강남 = stationDao.save(new Station("강남"));
        Station 양재 = stationDao.save(new Station("양재"));
        Station 광교 = stationDao.save(new Station("광교"));
        Line 신분당선 = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(신분당선, 강남, 광교, 10));

        SectionRequest sectionRequest = new SectionRequest(강남.getId(), 양재.getId(), 11);

        assertThatThrownBy(() -> lineService.createSection(신분당선.getId(), sectionRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("추가하려는 거리가 큽니다.");
    }

    private SectionEntity findSectionEntity(Station startStation, List<SectionEntity> sectionEntities) {
        return sectionEntities.stream()
            .filter(entity -> entity.getUpStationId().equals(startStation.getId()))
            .findFirst()
            .get();
    }
}
