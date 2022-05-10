package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionRepositoryTest extends RepositoryTest {

    @DisplayName("Section을 저장한다.")
    @Test
    void save() {
        Section section = new Section(1L, 신당역, 동묘앞역, 5);
        Long id = sectionRepository.save(section);

        assertThat(id).isNotNull();
    }

    @DisplayName("저장한 Section을 탐생한다.")
    @Test
    void findById() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Section section = new Section(1L, saved_신당역, saved_동묘앞역, 5);
        Long id = sectionRepository.save(section);
        Section foundSection = sectionRepository.findById(id);
        assertAll(
                () -> assertThat(foundSection.getUpStation()).isEqualTo(saved_신당역),
                () -> assertThat(foundSection.getDownStation()).isEqualTo(saved_동묘앞역)
        );
    }
}
