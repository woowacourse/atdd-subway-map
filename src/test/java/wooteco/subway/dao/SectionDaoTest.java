package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.fixture.LineFixture;
import wooteco.subway.domain.fixture.StationFixture;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private Long stationAId;
    private Long stationBId;
    private Long stationCId;
    private Long lineId;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(dataSource, jdbcTemplate);

        StationDao stationDao = new JdbcStationDao(dataSource, jdbcTemplate);
        stationAId = stationDao.save(StationEntity.from(StationFixture.STATION_A)).getId();
        stationBId = stationDao.save(StationEntity.from(StationFixture.STATION_B)).getId();
        stationCId = stationDao.save(StationEntity.from(StationFixture.STATION_C)).getId();

        LineDao lineDao = new JdbcLineDao(dataSource, jdbcTemplate);
        lineId = lineDao.save(LineEntity.from(LineFixture.LINE_A)).getId();
    }

    @Test
    @DisplayName("구간을 저장한다")
    public void saveNewSection() {
        // given
        SectionEntity entity = new SectionEntity(lineId, stationAId, stationBId, 7);
        // when
        final SectionEntity saved = sectionDao.save(entity);
        // then
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("노선에 저장된 구간을 읽는다.")
    public void readSectionsByLineId() {
        // given
        sectionDao.save(new SectionEntity(lineId, stationAId, stationBId, 7));
        // when
        final List<SectionEntity> sectionEntities = sectionDao.readSectionsByLineId(lineId);
        // then
        assertThat(sectionEntities.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간을 갱신한다.")
    public void update() {
        // given
        final SectionEntity savedEntity = sectionDao.save(new SectionEntity(lineId, stationAId, stationBId, 7));
        // when
        final SectionEntity updateEntity = new SectionEntity(
            savedEntity.getId(),
            savedEntity.getLineId(),
            stationCId,
            savedEntity.getDownStationId(),
            savedEntity.getDistance());
        // then
        assertThatCode(() -> sectionDao.update(updateEntity)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    public void delete() {
        // given
        final SectionEntity savedEntity = sectionDao.save(new SectionEntity(lineId, stationAId, stationBId, 7));

        // when
        final Long sectionId = savedEntity.getId();

        // then
        assertThatCode(() -> sectionDao.deleteById(sectionId)).doesNotThrowAnyException();
    }
}
