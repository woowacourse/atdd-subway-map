package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.RegisteredSectionEntity;
import wooteco.subway.entity.StationEntity;

@SuppressWarnings("NonAsciiCharacters")
class RegisteredSectionDaoTest extends DaoTest {

    private static final StationEntity STATION1 = new StationEntity(1L, "강남역");
    private static final StationEntity STATION2 = new StationEntity(2L, "선릉역");
    private static final StationEntity STATION3 = new StationEntity(3L, "잠실역");
    private static final LineEntity LINE1 = new LineEntity(1L, "1호선", "색깔");
    private static final LineEntity LINE2 = new LineEntity(2L, "2호선", "색깔2");

    @Autowired
    private RegisteredSectionDao dao;

    @BeforeEach
    void setup() {
        testFixtureManager.saveStations("강남역", "선릉역", "잠실역", "강변역", "청계산입구역");
        testFixtureManager.saveLine("1호선", "색깔");
        testFixtureManager.saveLine("2호선", "색깔2");
    }

    @Test
    void findAll_메서드는_등록된_모든_구간들의_데이터를_조회() {
        testFixtureManager.saveSection(1L, 1L, 2L, 20);
        testFixtureManager.saveSection(1L, 2L, 3L, 10);
        testFixtureManager.saveSection(2L, 1L, 3L, 30);

        List<RegisteredSectionEntity> actual = dao.findAll();
        List<RegisteredSectionEntity> expected = List.of(
                new RegisteredSectionEntity(LINE1, STATION1, STATION2, 20),
                new RegisteredSectionEntity(LINE1, STATION2, STATION3, 10),
                new RegisteredSectionEntity(LINE2, STATION1, STATION3, 30));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void findAllByLineId_메서드는_특정_노선에_등록된_모든_구간들의_데이터를_조회() {
        testFixtureManager.saveSection(1L, 1L, 2L, 20);
        testFixtureManager.saveSection(1L, 2L, 3L, 10);
        testFixtureManager.saveSection(2L, 1L, 3L, 30);

        List<RegisteredSectionEntity> actual = dao.findAllByLineId(1L);
        List<RegisteredSectionEntity> expected = List.of(
                new RegisteredSectionEntity(LINE1, STATION1, STATION2, 20),
                new RegisteredSectionEntity(LINE1, STATION2, STATION3, 10));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
