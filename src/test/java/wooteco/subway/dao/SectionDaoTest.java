package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixtures.GANGNAM;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SINSA;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.dao.entity.LineEntity;
import wooteco.subway.dao.entity.SectionEntity;
import wooteco.subway.dao.entity.StationEntity;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;
    private LineDao lineDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(dataSource);
        lineDao = new LineDao(dataSource);
        sectionDao = new SectionDao(dataSource);
    }

    @Test
    @DisplayName("지하철 구간을 저장한다.")
    void save() {
        final Long upStationId = stationDao.save(new StationEntity(HYEHWA));
        final Long downStationId = stationDao.save(new StationEntity(SINSA));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final SectionEntity SectionEntity = new SectionEntity(lineId, upStationId, downStationId, 10);

        final Long id = sectionDao.save(SectionEntity);
        final SectionEntity savedSection = sectionDao.findById(id);

        assertAll(() -> {
            assertThat(savedSection.getId()).isNotNull();
            assertThat(savedSection.getUpStationId()).isEqualTo(SectionEntity.getUpStationId());
            assertThat(savedSection.getDownStationId()).isEqualTo(SectionEntity.getDownStationId());
            assertThat(savedSection.getDistance()).isEqualTo(SectionEntity.getDistance());
        });
    }

    @Test
    @DisplayName("여러개의 지하철 구간을 저장한다.")
    void batchSave() {
        // given
        final Long stationId1 = stationDao.save(new StationEntity(HYEHWA));
        final Long stationId2 = stationDao.save(new StationEntity(SINSA));
        final Long stationId3 = stationDao.save(new StationEntity(GANGNAM));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final List<SectionEntity> sections = List.of(new SectionEntity(lineId, stationId1, stationId2, 10),
                new SectionEntity(lineId, stationId2, stationId3, 10));

        // when
        sectionDao.batchSave(sections);
        final List<SectionEntity> savedSections = sectionDao.findAllByLineId(lineId);

        // then
        assertThat(savedSections).hasSize(2);
    }

    @Test
    @DisplayName("지하철 역 ID로 모든 구간을 조회한다.")
    void findAllByLineId() {
        final Long stationId1 = stationDao.save(new StationEntity(HYEHWA));
        final Long stationId2 = stationDao.save(new StationEntity(SINSA));
        final Long stationId3 = stationDao.save(new StationEntity(GANGNAM));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final SectionEntity section1 = new SectionEntity(lineId, stationId1, stationId2, 10);
        final SectionEntity section2 = new SectionEntity(lineId, stationId2, stationId3, 10);

        sectionDao.save(section1);
        sectionDao.save(section2);

        final List<SectionEntity> sections = sectionDao.findAllByLineId(lineId);

        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("지하철 구간 ID로 해당 구간을 조회한다.")
    void findById() {
        // given
        final Long upStationId = stationDao.save(new StationEntity(HYEHWA));
        final Long downStationId = stationDao.save(new StationEntity(SINSA));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final SectionEntity SectionEntity = new SectionEntity(lineId, upStationId, downStationId, 10);
        final Long id = sectionDao.save(SectionEntity);

        // when
        final SectionEntity savedSection = sectionDao.findById(id);

        // then
        assertAll(() -> {
            assertThat(savedSection.getLineId()).isEqualTo(lineId);
            assertThat(savedSection.getUpStationId()).isEqualTo(upStationId);
            assertThat(savedSection.getDownStationId()).isEqualTo(downStationId);
            assertThat(savedSection.getDistance()).isEqualTo(10);
        });
    }

    @Test
    @DisplayName("지하철 구간 ID로 구간을 삭제한다.")
    void deleteById() {
        // given
        final Long upStationId = stationDao.save(new StationEntity(HYEHWA));
        final Long downStationId = stationDao.save(new StationEntity(SINSA));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final Long sectionId = sectionDao.save(new SectionEntity(lineId, upStationId, downStationId, 10));

        // when
        sectionDao.deleteById(sectionId);

        // then
        assertThat(sectionDao.findAllByLineId(lineId)).hasSize(0);
    }

    @Test
    @DisplayName("여러개의 지하철 구간을 삭제한다.")
    void batchDelete() {
        // given
        final Long stationId1 = stationDao.save(new StationEntity(HYEHWA));
        final Long stationId2 = stationDao.save(new StationEntity(SINSA));
        final Long stationId3 = stationDao.save(new StationEntity(GANGNAM));
        final Long lineId = lineDao.save(new LineEntity(LINE_2, RED));
        final List<SectionEntity> sections = List.of(new SectionEntity(lineId, stationId1, stationId2, 10),
                new SectionEntity(lineId, stationId2, stationId3, 10));

        // when
        sectionDao.batchDelete(sections);
        final List<SectionEntity> savedSections = sectionDao.findAllByLineId(lineId);

        // then
        assertThat(savedSections).hasSize(0);
    }
}
