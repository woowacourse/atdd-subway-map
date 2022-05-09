package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.ui.dto.SectionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class SectionDaoTest {

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("구간 생성")
    @Test
    void save() {
        // given
        SectionRequest section = new SectionRequest(1L, 1L, 2L, 10);

        // when
        Long id = sectionDao.save(section);

        // then
        assertThat(id).isEqualTo(1L);
    }
}