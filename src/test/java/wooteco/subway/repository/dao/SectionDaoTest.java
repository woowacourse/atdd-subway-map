package wooteco.subway.repository.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

import javax.sql.DataSource;
import wooteco.subway.repository.dao.entity.section.SectionEntity;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private DataSource dataSource;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(dataSource);
    }

    @DisplayName("지하철구간을 저장한다.")
    @Test
    void save() {
        SectionEntity sectionEntity = new SectionEntity(0L, 1L, 1L, 2L, 10);
        Long sectionId = sectionDao.save(sectionEntity);
        assertThat(sectionId).isGreaterThan(0L);
    }

    @DisplayName("지하철노선별 구간 목록을 조회한다.")
    @Test
    void findAllByLineId() {
        Long anotherLineId = 2L;
        sectionDao.save(new SectionEntity(0L, anotherLineId, 2L, 3L, 10));

        Long expectedLineId = 1L;
        sectionDao.save(new SectionEntity(0L, expectedLineId, 1L, 2L, 10));
        sectionDao.save(new SectionEntity(0L, expectedLineId, 2L, 3L, 10));
        assertThat(sectionDao.findAllByLineId(expectedLineId)).hasSize(2);
    }

    @DisplayName("지하철구간을 삭제한다.")
    @Test
    void remove() {
        Long lineId = 1L;
        Long sectionId = sectionDao.save(new SectionEntity(0L, lineId, 1L, 2L, 10));
        sectionDao.remove(sectionId);
        assertThat(sectionDao.findAllByLineId(lineId)).isEmpty();
    }
}