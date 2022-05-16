package wooteco.subway.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineInfo;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SubwayRepositoryTest extends RepositoryTest {

    @Autowired
    private SubwayRepository repository;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    private final Station station1 = new Station(1L, "강남역");
    private final Station station2 = new Station(2L, "잠실역");
    private final Station station3 = new Station(3L, "선릉역");
    private final Station station4 = new Station(4L, "청계산입구역");
    private final StationEntity stationEntity1 = new StationEntity(1L, "강남역");
    private final StationEntity stationEntity2 = new StationEntity(2L, "잠실역");
    private final StationEntity stationEntity3 = new StationEntity(3L, "선릉역");
    private final StationEntity stationEntity4 = new StationEntity(4L, "청계산입구역");

    @BeforeEach
    void setup() {
        testFixtureManager.saveStations("강남역", "잠실역", "선릉역", "청계산입구역");
    }

    @Test
    void findAllLines_메서드는_모든_노선_정보들을_조회하여_도메인들의_리스트로_반환() {
        testFixtureManager.saveLine("노선명1", "색깔1");
        testFixtureManager.saveLine("노선명2", "색깔2");
        testFixtureManager.saveLine("노선명3", "색깔3");

        List<LineInfo> actual = repository.findAllLines();
        List<LineInfo> expected = List.of(
                new LineInfo(1L, "노선명1", "색깔1"),
                new LineInfo(2L, "노선명2", "색깔2"),
                new LineInfo(3L, "노선명3", "색깔3"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllSections_메서드는_모든_구간_정보들을_조회하여_도메인들의_리스트로_반환() {
        testFixtureManager.saveSection(1L, 1L, 2L, 10);
        testFixtureManager.saveSection(1L, 2L, 3L, 15);
        testFixtureManager.saveSection(2L, 1L, 3L, 5);

        List<Section> actual = repository.findAllSections();
        List<Section> expected = List.of(
                new Section(1L, station1, station2, 10),
                new Section(1L, station2, station3, 15),
                new Section(2L, station1, station3, 5));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllSectionsByLineId_메서드는_특정_노선에_등록된_모든_구간_정보들을_조회하여_도메인들의_리스트로_반환() {
        testFixtureManager.saveSection(1L, 1L, 2L, 10);
        testFixtureManager.saveSection(1L, 2L, 3L, 15);
        testFixtureManager.saveSection(2L, 1L, 3L, 5);

        List<Section> actual = repository.findAllSectionsByLineId(1L);
        List<Section> expected = List.of(
                new Section(1L, station1, station2, 10),
                new Section(1L, station2, station3, 15));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findAllSectionsByStationId_메서드는_특정_지하철역이_등록된_모든_구간_정보들을_조회하여_도메인들의_리스트로_반환() {
        testFixtureManager.saveSection(1L, 1L, 2L, 10);
        testFixtureManager.saveSection(1L, 2L, 3L, 15);
        testFixtureManager.saveSection(2L, 1L, 3L, 5);

        List<Section> actual = repository.findAllSectionsByStationId(1L);
        List<Section> expected = List.of(
                new Section(1L, station1, station2, 10),
                new Section(2L, station1, station3, 5));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("findExistingLine 메서드는 id에 대응되는 노선을 조회")
    @Nested
    class FindExistingLineTest {

        @Test
        void id에_대응되는_노선이_존재하는_경우_도메인으로_반환() {
            testFixtureManager.saveLine("노선1", "색상");
            LineInfo actual = repository.findExistingLine(1L);
            LineInfo expected = new LineInfo(1L, "노선1", "색상");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void id에_대응되는_노선이_존재하지_않는_경우_예외_발생() {
            assertThatThrownBy(() -> repository.findExistingLine(1L))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    @DisplayName("checkExistingLine 메서드는 해당 id에 대응되는 노선의 존재 여부를 반환")
    @Nested
    class CheckExistingLineTest {

        @Test
        void 존재하는_노선의_id인_경우_참_반환() {
            testFixtureManager.saveLine("존재", "색상");
            boolean actual = repository.checkExistingLine(1L);

            assertThat(actual).isTrue();
        }

        @Test
        void 존재하지_않는_노선의_id인_경우_거짓_반환() {
            boolean actual = repository.checkExistingLine(1L);

            assertThat(actual).isFalse();
        }
    }

    @DisplayName("checkExistingLineName 메서드는 해당 이름의 노선의 존재 여부를 반환")
    @Nested
    class CheckExistingLineNameTest {

        @Test
        void 존재하는_노선의_이름인_경우_참_반환() {
            testFixtureManager.saveLine("이름!", "색상");
            boolean actual = repository.checkExistingLineName("이름!");

            assertThat(actual).isTrue();
        }

        @Test
        void 존재하지_않는_노선의_이름인_경우_거짓_반환() {
            boolean actual = repository.checkExistingLineName("없는 이름");

            assertThat(actual).isFalse();
        }
    }

    @DisplayName("saveLine 메서드는 새로운 노선과 구간을 저장하여 반환")
    @Nested
    class SaveLineTest {

        @Test
        void 생성된_노선의_도메인을_반환() {
            LineInfo lineInfo = new LineInfo("노선", "색상");
            Section initialSection = new Section(1L, station1, station2, 10);

            Line actual = repository.saveLine(lineInfo, initialSection);
            Line expected = Line.of(new LineInfo(1L, "노선", "색상"), initialSection);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void 새로운_노선과_구간을_저장() {
            LineInfo lineInfo = new LineInfo("노선", "색상");
            Section initialSection = new Section(1L, station1, station2, 10);
            repository.saveLine(lineInfo, initialSection);

            LineEntity actualLine = lineDao.findById(1L).get();
            List<SectionEntity> actualSections = sectionDao.findAll();
            LineEntity expectedLine = new LineEntity(1L, "노선", "색상");
            List<SectionEntity> expectedSections = List.of(
                    new SectionEntity(1L, stationEntity1, stationEntity2, 10));

            assertThat(actualLine).isEqualTo(expectedLine);
            assertThat(actualSections).isEqualTo(expectedSections);
        }
    }

    @Test
    void saveSections_메서드는_구간들을_저장() {
        List<Section> sections = List.of(
                new Section(station1, station2, 10),
                new Section(station2, station3, 5));
        repository.saveSections(1L, sections);

        List<SectionEntity> actual = sectionDao.findAll();
        List<SectionEntity> expected = List.of(
                new SectionEntity(1L, stationEntity1, stationEntity2, 10),
                new SectionEntity(1L, stationEntity2, stationEntity3, 5));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateLine_메서드는_노선_정보를_수정() {
        testFixtureManager.saveLine("기존 노선명", "색상");

        repository.updateLine(new LineInfo(1L, "새로운 노선명", "새로운 색상"));
        LineEntity actual = lineDao.findById(1L).get();
        LineEntity expected = new LineEntity(1L, "새로운 노선명", "새로운 색상");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteLine_메서드는_노선과_등록된_구간들을_제거() {
        testFixtureManager.saveLine("노선1", "색상");
        testFixtureManager.saveSection(1L, 1L, 2L, 10);
        testFixtureManager.saveSection(1L, 2L, 3L, 15);

        repository.deleteLine(new LineInfo(1L, "노선1", "색상"));
        boolean lineExistence = lineDao.findById(1L).isPresent();
        List<SectionEntity> existingSections = sectionDao.findAll();

        assertThat(lineExistence).isFalse();
        assertThat(existingSections).isEmpty();
    }

    @Test
    void deleteSections_메서드는_구간들을_제거() {
        testFixtureManager.saveSection(1L, 1L, 2L, 10);
        testFixtureManager.saveSection(1L, 2L, 3L, 15);
        testFixtureManager.saveSection(1L, 3L, 4L, 5);
        testFixtureManager.saveSection(2L, 1L, 3L, 5);
        List<Section> sections = List.of(new Section(station1, station2, 10),
                new Section(station2, station3, 15));

        repository.deleteSections(1L, sections);
        List<SectionEntity> actual = sectionDao.findAll();
        List<SectionEntity> expected = List.of(
                new SectionEntity(1L, stationEntity3, stationEntity4, 5),
                new SectionEntity(2L, stationEntity1, stationEntity3, 5));

        assertThat(actual).isEqualTo(expected);
    }
}
