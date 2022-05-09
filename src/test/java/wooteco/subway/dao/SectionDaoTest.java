package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
}
