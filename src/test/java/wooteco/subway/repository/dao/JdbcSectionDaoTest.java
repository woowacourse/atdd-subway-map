package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.repository.entity.SectionEntity;

@Sql("/jdbcSectionDaoSetting.sql")
@JdbcTest
class JdbcSectionDaoTest {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(namedParameterJdbcTemplate);
    }

    @DisplayName("구간을 저장하고 id로 구간들을 찾는다.")
    @Test
    void saveAndFindById() {
        SectionEntity sectionEntity_GN_YS = new SectionEntity(null, 1L, 1L, 3L, 100);

        List<SectionEntity> savedSectionEntities =
                sectionDao.findByLineId(sectionDao.save(sectionEntity_GN_YS).getLineId());

        assertAll(
                () -> assertThat(savedSectionEntities.size()).isEqualTo(1),
                () -> assertThat(savedSectionEntities.get(0).getUpStationId()).isEqualTo(1L),
                () -> assertThat(savedSectionEntities.get(0).getDownStationId()).isEqualTo(3L)
        );
    }

    @DisplayName("역 id 로 구간들을 찾는다.")
    @Test
    void findByStationId() {
        SectionEntity sectionEntity_GN_YS = new SectionEntity(null, 1L, 1L, 2L, 50);
        SectionEntity sectionEntity_YS_SL = new SectionEntity(null, 1L, 2L, 3L, 50);
        sectionDao.save(sectionEntity_GN_YS);
        sectionDao.save(sectionEntity_YS_SL);

        List<SectionEntity> savedSectionEntities = sectionDao.findByStationId(2L);

        assertThat(savedSectionEntities.size()).isEqualTo(2);
    }

    @DisplayName("구간을 수정한다.")
    @Test
    void update() {
        SectionEntity sectionEntity_GN_YS = new SectionEntity(null, 1L, 1L, 2L, 50);
        sectionDao.save(sectionEntity_GN_YS).getLineId();

        sectionDao.update(new SectionEntity(1L, 1L, 2L, 3L, 30));

        assertAll(
                () -> assertThat(sectionDao.findByLineId(1L).get(0).getUpStationId()).isEqualTo(2L),
                () -> assertThat(sectionDao.findByLineId(1L).get(0).getDownStationId()).isEqualTo(3L),
                () -> assertThat(sectionDao.findByLineId(1L).get(0).getDistance()).isEqualTo(30)
        );
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteByLineIdAndStationId() {
        SectionEntity sectionEntity_GN_YS = new SectionEntity(null, 1L, 1L, 2L, 50);
        SectionEntity sectionEntity_YS_SL = new SectionEntity(null, 1L, 2L, 3L, 50);
        sectionDao.save(sectionEntity_GN_YS);
        sectionDao.save(sectionEntity_YS_SL);

        sectionDao.deleteByLineIdAndStationId(1L, 2L);

        assertThat(sectionDao.findByLineId(1L).size()).isEqualTo(0);
    }
}
