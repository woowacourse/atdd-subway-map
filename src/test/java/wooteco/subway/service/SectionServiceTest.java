package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionServiceTest {

    private final SectionService sectionService;
    private final SectionDao sectionDao;

    public SectionServiceTest(SectionService sectionService, SectionDao sectionDao) {
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
    }

    @AfterEach
    void reset() {
        sectionDao.deleteAll();
    }

    @Test
    @DisplayName("기존의 구간의 길이와 같은 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_same() {
        sectionDao.save(1L, 1L, 2L, 5);

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존의 구간의 길이보다 큰 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_longer() {
        sectionDao.save(1L, 1L, 2L, 5);

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 8);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }
}
