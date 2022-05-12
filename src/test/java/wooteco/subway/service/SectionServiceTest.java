package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.WooTecoException;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@Transactional
public class SectionServiceTest {
    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        sectionDao.save(new Section(1L, 2L, 15L), 1L);
        sectionDao.save(new Section(2L, 3L, 15L), 1L);
    }

    @DisplayName("하행역이 변하지 않고 거리가 초과되는 구간을 등록할 때 예외가 발생하는지 테스트")
    @Test
    void save_not_change_down_end_dist_over() {
        assertThatThrownBy(() -> sectionService.save(new SectionRequest(4L, 2L, 100L), 1L))
                .isInstanceOf(WooTecoException.class);
    }

    @DisplayName("상행역이 변하지 않고 거리가 초과되는 구간을 등록할 때 예외가 발생하는지 테스트")
    @Test
    void save_not_change_up_end_dist_over() {
        assertThatThrownBy(() -> sectionService.save(new SectionRequest(4L, 3L, 100L), 1L))
                .isInstanceOf(WooTecoException.class);
    }

    @DisplayName("이미 존재하는 구간을 등록할 때 예외가 발생하는지 테스트")
    @Test
    void save_exist() {
        assertThatThrownBy(() -> sectionService.save(new SectionRequest(2L, 3L, 10L), 1L))
                .isInstanceOf(WooTecoException.class);
    }

    @DisplayName("전체 사이클이 아닌 사이클을 형성하는 구간을 등록할 때 예외가 발생하는지 테스트")
    @Test
    void save_sub_cycle() {
        assertThatThrownBy(() -> sectionService.save(new SectionRequest(3L, 2L, 10L), 1L))
                .isInstanceOf(WooTecoException.class);
    }

    @DisplayName("두 역을 모두 공유하지 않는 구간을 등록할 때 예외가 발생하는지 테스트")
    @Test
    void save_no_exist() {
        assertThatThrownBy(() -> sectionService.save(new SectionRequest(4L, 5L, 10L), 1L))
                .isInstanceOf(WooTecoException.class);
    }

    @DisplayName("상행선을 바꾸지 않고 거리 조건을 만족하는 구간을 등록할 때 정확히 저장하는지 테스트")
    @Test
    void save_not_change_up() {
        Section result = sectionService.save(new SectionRequest(1L, 4L, 10L), 1L);
        assertThat(result).isNotNull();
    }

    @DisplayName("상행선을 바꾸고 거리 조건을 만족하는 구간을 등록할 때 정확히 저장하는지 테스트")
    @Test
    void save_change_up() {
        Section result = sectionService.save(new SectionRequest(4L, 1L, 10L), 1L);
        assertThat(result).isNotNull();
    }

    @DisplayName("하행선을 바꾸지 않고 거리 조건을 만족하는 구간을 등록할 때 정확히 저장하는지 테스트")
    @Test
    void save_not_change_down() {
        Section result = sectionService.save(new SectionRequest(4L, 3L, 10L), 1L);
        assertThat(result).isNotNull();
    }

    @DisplayName("하행선을 바꾸고 거리 조건을 만족하는 구간을 등록할 때 정확히 저장하는지 테스트")
    @Test
    void save_change_down() {
        Section result = sectionService.save(new SectionRequest(3L, 4L, 10L), 1L);
        assertThat(result).isNotNull();
    }

    @DisplayName("전체 사이클을 형성하는 구간을 등록할 때 정확히 저장하는지 테스트")
    @Test
    void save_cycle() {
        Section result = sectionService.save(new SectionRequest(3L, 1L, 10L), 1L);
        assertThat(result).isNotNull();
    }

    @DisplayName("일반 구간의 종점을 삭제할 때 정확히 삭제하는지 테스트")
    @Test
    void delete_end_point() {
        sectionService.delete(1L, 1L);
        assertThat(sectionDao.findAll(1L).size()).isEqualTo(1);
    }

    @DisplayName("일반 구간의 종점이 아닌 점을 삭제할 때 정확히 삭제하는지 테스트")
    @Test
    void delete_not_end_point() {
        sectionService.delete(1L, 2L);
        assertThat(sectionDao.findAll(1L).size()).isEqualTo(1);
    }

    @DisplayName("사이클을 형성하는 구간을 삭제할 때 정확히 삭제하는지 테스트")
    @Test
    void delete_cycle() {
        sectionService.save(new SectionRequest(3L, 1L, 10L), 1L);
        sectionService.delete(1L, 3L);
        assertThat(sectionDao.findAll(1L).size()).isEqualTo(1);
    }
}
