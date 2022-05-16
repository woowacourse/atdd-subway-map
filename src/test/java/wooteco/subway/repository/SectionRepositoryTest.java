package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.STANDARD_DISTANCE;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

class SectionRepositoryTest extends RepositoryTest {

    @DisplayName("Section을 저장한다.")
    @Test
    void save() {
        Section section = new Section(1L, 신당역, 동묘앞역, STANDARD_DISTANCE);
        Long id = sectionRepository.save(section);

        assertThat(id).isNotNull();
    }

    @DisplayName("저장한 Section을 탐생한다.")
    @Test
    void findById() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);

        Section section = new Section(1L, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        Long id = sectionRepository.save(section);
        Section foundSection = sectionRepository.findById(id);
        assertAll(
                () -> assertThat(foundSection.getUpStation()).isEqualTo(saved_신당역),
                () -> assertThat(foundSection.getDownStation()).isEqualTo(saved_동묘앞역)
        );
    }

    @DisplayName("Section을 update 한다.")
    @Test
    void update() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Station saved_창신역 = stationRepository.save(창신역);

        Section section = new Section(1L, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        Long id = sectionRepository.save(section);
        sectionRepository.update(new Section(id, 1L, saved_신당역, saved_창신역, STANDARD_DISTANCE - 1));
        Section foundSection = sectionRepository.findById(id);
        assertAll(
                () -> assertThat(foundSection.getUpStation()).isEqualTo(saved_신당역),
                () -> assertThat(foundSection.getDownStation()).isEqualTo(saved_창신역),
                () -> assertThat(foundSection.getDistance()).isEqualTo(STANDARD_DISTANCE - 1)
        );
    }

    @DisplayName("List로 들어온 Section을 모두 삭제한다.")
    @Test
    void deleteSections() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Station saved_창신역 = stationRepository.save(창신역);

        Section firstSection = new Section(1L, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        Long id1 = sectionRepository.save(firstSection);

        Section secondSection = new Section(1L, saved_신당역, saved_창신역, STANDARD_DISTANCE);
        Long id2 = sectionRepository.save(secondSection);

        sectionRepository.deleteSections(List.of(new Section(id1, 1L, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE),
                new Section(id2, 1L, saved_신당역, saved_창신역, STANDARD_DISTANCE)));

        assertThat(sectionRepository.findByLineId(1L)).isEmpty();
    }

    @DisplayName("Section에 특정 Station이 존재하는지 확인한다.")
    @Test
    void findStationExist() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);

        Section firstSection = new Section(1L, saved_신당역, saved_동묘앞역, STANDARD_DISTANCE);
        Long id = sectionRepository.save(firstSection);

        assertThat(sectionRepository.isStationExist(saved_동묘앞역.getId())).isTrue();
    }

}
