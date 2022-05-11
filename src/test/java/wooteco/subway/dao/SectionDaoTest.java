package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import wooteco.subway.entity.SectionEntity;

@JdbcTest
class SectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    SectionDaoTest(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void saveSection() {
        SectionEntity sectionEntity = new SectionEntity.Builder(1L, 1L, 2L, 10)
                .build();
        SectionEntity savedSectionEntity = sectionDao.save(sectionEntity);

        assertAll(
                () -> assertThat(savedSectionEntity.getId()).isNotZero(),
                () -> assertThat(savedSectionEntity.getLineId()).isEqualTo(1L),
                () -> assertThat(savedSectionEntity.getUpStationId()).isEqualTo(1L),
                () -> assertThat(savedSectionEntity.getDownStationId()).isEqualTo(2L),
                () -> assertThat(savedSectionEntity.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("구간을 해당하는 노선 아이디로 모두 조회한다.")
    @Test
    void findAllSectionByLineId() {
        SectionEntity sectionEntity = new SectionEntity.Builder(1L, 1L, 2L, 10)
                .build();
        SectionEntity sectionEntity2 = new SectionEntity.Builder(1L, 2L, 3L, 8)
                .build();
        sectionDao.save(sectionEntity);
        sectionDao.save(sectionEntity2);

        List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(1L);

        assertThat(sectionEntities).hasSize(2);
    }

    @DisplayName("특정 구간을 삭제한다.")
    @Test
    void deleteById() {
        SectionEntity sectionEntity = new SectionEntity.Builder(1L, 1L, 2L, 10)
                .build();
        SectionEntity savedSectionEntity = sectionDao.save(sectionEntity);
        sectionDao.deleteById(savedSectionEntity.getId());

        List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(1L);
        assertThat(sectionEntities).isEmpty();
    }
}
