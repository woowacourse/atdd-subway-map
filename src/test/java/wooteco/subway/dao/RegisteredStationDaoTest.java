package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wooteco.subway.entity.LineEntity;
import wooteco.subway.entity.StationEntity;
import wooteco.subway.entity.RegisteredStationEntity;

@SuppressWarnings("NonAsciiCharacters")
class RegisteredStationDaoTest extends DaoTest {

    private static final LineEntity LINE1 = new LineEntity(1L, "이미 존재하는 노선 이름", "노란색");
    private static final LineEntity LINE2 = new LineEntity(2L, "신분당선", "빨간색");
    private static final StationEntity STATION1 = new StationEntity(1L, "이미 존재하는 역 이름");
    private static final StationEntity STATION2 = new StationEntity(2L, "선릉역");
    private static final StationEntity STATION3 = new StationEntity(3L, "잠실역");

    @Autowired
    private RegisteredStationDao dao;

    @Test
    void findAll_메서드는_역에_연결된_노선을_포함한_모든_데이터를_조회() {
        List<RegisteredStationEntity> actual = dao.findAll();

        List<RegisteredStationEntity> expected = List.of(
                new RegisteredStationEntity(STATION1, LINE1),
                new RegisteredStationEntity(STATION2, LINE1),
                new RegisteredStationEntity(STATION3, LINE1),
                new RegisteredStationEntity(STATION1, LINE2),
                new RegisteredStationEntity(STATION3, LINE2));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void findAllByStationId_메서드는_해당_역이_등록된_모든_노선들의_데이터를_조회() {
        List<RegisteredStationEntity> actual = dao.findAllByStationId(1L);

        List<RegisteredStationEntity> expected = List.of(
                new RegisteredStationEntity(STATION1, LINE1),
                new RegisteredStationEntity(STATION1, LINE2));

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}