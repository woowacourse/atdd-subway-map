package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class SectionDaoTest {
    @Autowired
    private SectionDao sectionDao;

    @Test
    @DisplayName("Section을 정상적으로 생성하는 지 테스트")
    public void createSection() {
        Long id = sectionDao.create(1L, 1L, 2L, 5);
        assertThat(sectionDao.findById(id).isPresent()).isTrue();
    }

    @Test
    @DisplayName("모든 Section을 정상적으로 조회하는 지 테스트")
    public void findAll() {
        sectionDao.create(1L, 1L, 2L, 5);
        sectionDao.create(1L, 1L, 2L, 5);
        assertThat(sectionDao.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("특정 Section을 정상적으로 조회하는 지 테스트")
    public void findById() {
        long lineId = 1L;
        long upStationId = 1L;
        long downStationId = 2L;
        int distance = 5;
        Long id = sectionDao.create(lineId, upStationId, downStationId, distance);
        Optional<Section> section = sectionDao.findById(id);
        assertThat(section.get().getLineId()).isEqualTo(lineId);
        assertThat(section.get().getUpStationId()).isEqualTo(upStationId);
        assertThat(section.get().getDownStationId()).isEqualTo(downStationId);
        assertThat(section.get().getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("특정 Section을 정상적으로 수정하는 지 테스트")
    public void edit() {
        Long id = sectionDao.create(1L, 1L, 2L, 5);
        long changedLineId = 100L;
        long changedUpStationId = 100L;
        long changedDownStationId = 200L;
        int changedDistance = 500;
        int changedRowCount = sectionDao.edit(
                id,
                changedLineId,
                changedUpStationId,
                changedDownStationId,
                changedDistance
        );
        Optional<Section> section = sectionDao.findById(id);
        assertThat(changedRowCount).isEqualTo(1);
        assertThat(section.get().getLineId()).isEqualTo(changedLineId);
        assertThat(section.get().getUpStationId()).isEqualTo(changedUpStationId);
        assertThat(section.get().getDownStationId()).isEqualTo(changedDownStationId);
        assertThat(section.get().getDistance()).isEqualTo(changedDistance);
    }

    @Test
    @DisplayName("특정 Section을 정상적으로 삭제하는 지 테스트")
    public void deleteById() {
        Long id = sectionDao.create(1L, 1L, 2L, 5);
        sectionDao.deleteById(id);
        assertThat(sectionDao.findById(id).isPresent()).isFalse();
    }
}
