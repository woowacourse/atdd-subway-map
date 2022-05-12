package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.TestFixtures.동묘앞역;
import static wooteco.subway.TestFixtures.보문역;
import static wooteco.subway.TestFixtures.신당역;
import static wooteco.subway.TestFixtures.창신역;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.utils.exception.NameDuplicatedException;

public class LineRepositoryTest extends RepositoryTest {

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Line line = new Line("분당선", "bg-red-600");
        Long id = lineRepository.save(line);
        assertThat(id).isNotNull();
    }

    @DisplayName("노선 저장시 유니크 키에 위반되면 에러를 발생한다")
    @Test
    void saveUniqueException() {
        Line line = new Line("분당선", "bg-red-600");
        lineRepository.save(line);

        assertThatThrownBy(() -> lineRepository.save(line))
                .isInstanceOf(NameDuplicatedException.class);
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        Station saved_신당역 = stationRepository.save(신당역);
        Station saved_동묘앞역 = stationRepository.save(동묘앞역);
        Line line1 = new Line("분당선", "bg-red-600");
        Long id1 = lineRepository.save(line1);
        Section section1 = new Section(id1, saved_신당역, saved_동묘앞역, 5);
        sectionRepository.save(section1);

        Station saved_보문역 = stationRepository.save(보문역);
        Station saved_창신역 = stationRepository.save(창신역);
        Line line2 = new Line("신분당선", "bg-red-600");
        Long id2 = lineRepository.save(line2);
        Section section2 = new Section(id2, saved_보문역, saved_창신역, 5);
        sectionRepository.save(section2);

        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(2);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findById() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line line = lineRepository.findById(id);
        assertThat(line.isSameName("분당선"));
    }

    @DisplayName("이름으로 노선을 조회한다.")
    @Test
    void findByName() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line line = lineRepository.findByName("분당선").orElse(null);

        assertThat(line.getId()).isEqualTo(id);
    }

    @DisplayName("이름으로 노선을 조회시 없을 경우 빈 Optional을 반환한다.")
    @Test
    void findByNameNull() {
        assertThat(lineRepository.findByName("분당선").isEmpty()).isTrue();
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.update(new Line(id, "신분당선", "bg-yellow-600"));
        Line findUpdateLine = lineRepository.findById(id);

        assertAll(
                () -> assertThat(findUpdateLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(findUpdateLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteById() {
        Long id = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.deleteById(id);

        assertThat(lineRepository.findAll()).isEmpty();
    }
}
