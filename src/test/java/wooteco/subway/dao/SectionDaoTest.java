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
        stationAId = stationDao.save(StationEntity.from(StationFixture.getStationA()));
        stationBId = stationDao.save(StationEntity.from(StationFixture.getStationB()));
        stationCId = stationDao.save(StationEntity.from(StationFixture.getStationC()));

        LineDao lineDao = new JdbcLineDao(dataSource, jdbcTemplate);
        lineId = lineDao.save(LineEntity.from(LineFixture.getLineAb()));
    }

    @Test
    @DisplayName("구간을 저장한다")
    public void saveNewSection() {
        // given
        SectionEntity entity = new SectionEntity(lineId, stationAId, stationBId, 7);
        // when
        final Long id = sectionDao.save(entity);
        // then
        assertThat(id).isNotNull();
    }

    @Test
    @DisplayName("노선에 저장된 구간을 읽는다.")
    public void readSectionsByLineId() {
        // given
        sectionDao.save(new SectionEntity(lineId, stationAId, stationBId, 7));
        // when
        final List<SectionEntity> sectionEntities = sectionDao.findSectionsByLineId(lineId);
        // then
        assertThat(sectionEntities.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    public void delete() {
        // given & when
        final Long id = sectionDao.save(new SectionEntity(lineId, stationAId, stationBId, 7));

        // then
        assertThatCode(() -> sectionDao.delete(id)).doesNotThrowAnyException();
    }
}
