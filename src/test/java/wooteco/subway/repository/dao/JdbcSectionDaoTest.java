package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.repository.entity.LineEntity;
import wooteco.subway.repository.entity.SectionEntity;
import wooteco.subway.repository.entity.StationEntity;
import wooteco.subway.service.LineService;

@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(namedParameterJdbcTemplate);
        lineDao = new JdbcLineDao(namedParameterJdbcTemplate);
        stationDao = new JdbcStationDao(namedParameterJdbcTemplate);
    }

    @DisplayName("구간을 저장하고 id로 구간을 찾는다.")
    @Test
    void saveAndFindById() {
        final Station station1 = Station.createWithoutId("선릉역");
        final StationEntity savedStation1 = stationDao.save(new StationEntity(station1));
        final Station station2 = Station.createWithoutId("잠실역");
        final StationEntity savedStation2 = stationDao.save(new StationEntity(station2));
        final Section section = Section.createWithoutId(
                new Station(savedStation1.getId(), savedStation1.getName()),
                new Station(savedStation2.getId(), savedStation2.getName()),
                10
        );
        final Line line = Line.createWithoutId("2호선", "bg-green-600");
        final LineEntity lineEntity = lineDao.save(new LineEntity(line));
        final SectionEntity saved = sectionDao.save(new SectionEntity(section, lineEntity.getId()));

        final SectionEntity find = sectionDao.findById(saved.getId()).get();

        assertAll(
                () -> assertThat(find.getId()).isEqualTo(saved.getId()),
                () -> assertThat(find.getLineId()).isEqualTo(saved.getLineId()),
                () -> assertThat(find.getUpStationId()).isEqualTo(saved.getUpStationId()),
                () -> assertThat(find.getDownStationId()).isEqualTo(saved.getDownStationId()),
                () -> assertThat(find.getDistance()).isEqualTo(saved.getDistance())
        );
    }

    @DisplayName("id로 구간을 삭제한다.")
    @Test
    void deleteById() {
        final Station station1 = Station.createWithoutId("선릉역");
        final StationEntity savedStation1 = stationDao.save(new StationEntity(station1));
        final Station station2 = Station.createWithoutId("잠실역");
        final StationEntity savedStation2 = stationDao.save(new StationEntity(station2));
        final Section section = Section.createWithoutId(
                new Station(savedStation1.getId(), savedStation1.getName()),
                new Station(savedStation2.getId(), savedStation2.getName()),
                10
        );
        final Line line = Line.createWithoutId("2호선", "bg-green-600");
        final LineEntity lineEntity = lineDao.save(new LineEntity(line));
        final SectionEntity saved = sectionDao.save(new SectionEntity(section, lineEntity.getId()));

        sectionDao.deleteById(saved.getId());
        final Optional<SectionEntity> empty = sectionDao.findById(saved.getId());

        assertThat(empty.isEmpty()).isTrue();
    }
}