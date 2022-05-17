package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.repository.dao.JdbcSectionDao;
import wooteco.subway.repository.entity.SectionEntity;

@JdbcTest
public class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcSectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);
    }

    @Test
    @DisplayName("Section 객체를 저장하기")
    void save() {
        // given
        SectionEntity section = new SectionEntity(null, 1L, 1L, 2L, 1);

        // when
        SectionEntity savedSection = sectionDao.save(section);

        // then
        assertAll(() -> {
            assertThat(savedSection.getId()).isNotNull();
            assertThat(savedSection.getUpStationId()).isEqualTo(section.getUpStationId());
            assertThat(savedSection.getDownStationId()).isEqualTo(section.getDownStationId());
            assertThat(savedSection.getDistance()).isEqualTo(section.getDistance());
        });
    }

    @Test
    @DisplayName("특정 노선의 구간 반환하기")
    void findByLineId() {
        // given
        SectionEntity savedSection1 = sectionDao.save(new SectionEntity(null, 1L, 1L, 2L, 1));
        SectionEntity savedSection2 = sectionDao.save(new SectionEntity(null, 1L, 2L, 3L, 2));

        // when
        List<SectionEntity> sections = sectionDao.findByLineId(1L);

        // then
        assertThat(sections).hasSize(2);
    }

    @Test
    @DisplayName("특정 노선 삭제하기")
    void delete() {
        // given
        SectionEntity savedSection = sectionDao.save(new SectionEntity(null, 1L, 1L, 2L, 1));

        // when
        int deletedSections = sectionDao.deleteById(savedSection.getId());

        // then
        assertThat(deletedSections).isOne();
    }

    @Test
    @DisplayName("특정 노선에 해당하는 구간 삭제하기")
    void deleteByLineId() {
        // given
        long lineId = 1L;
        sectionDao.save(new SectionEntity(null, lineId, 1L, 2L, 1));
        sectionDao.save(new SectionEntity(null, lineId, 2L, 3L, 1));

        // when
        int affectedRows = sectionDao.deleteByLineId(lineId);

        // then
        assertThat(affectedRows).isEqualTo(2);
    }

    @Test
    @DisplayName("구간 정보를 수정하기")
    void update() {
        // given
        SectionEntity section = sectionDao.save(new SectionEntity(null, 1L, 1L, 2L, 1));
        SectionEntity sectionForUpdate = new SectionEntity(section.getId(), 1L, 1L, 3L, 3);

        // when
        int updated = sectionDao.update(sectionForUpdate);

        // then
        assertThat(updated).isOne();
    }

    @Test
    @DisplayName("구간들을 한번에 저장하기")
    void saveAll() {
        // given
        SectionEntity entity1 = new SectionEntity(null, 1L, 1L, 2L, 1);
        SectionEntity entity2 = new SectionEntity(null, 1L, 2L, 3L, 1);
        List<SectionEntity> entities = List.of(entity1, entity2);

        // when
        int affectedRows = sectionDao.saveAll(entities);

        // then
        assertThat(affectedRows).isEqualTo(2);
    }

    @Test
    @DisplayName("해당 역 id를 갖는 구간 찾기")
    void findByStationId() {
        // given
        SectionEntity entity1 = new SectionEntity(null, 1L, 1L, 2L, 1);
        SectionEntity entity2 = new SectionEntity(null, 1L, 2L, 3L, 1);
        List<SectionEntity> entities = List.of(entity1, entity2);
        sectionDao.saveAll(entities);

        // when
        List<SectionEntity> sections = sectionDao.findByStationId(1L, 2L);

        // then
        assertThat(sections).hasSize(2);
    }
}
