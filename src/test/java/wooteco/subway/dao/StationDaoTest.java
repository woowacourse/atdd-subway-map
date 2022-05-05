package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

class StationDaoTest {

    private static final String STATION_NAME = "청구역";

    @Test
    @DisplayName("역을 저장한다.")
    public void save() {
        // given
        StationDao dao = new MemoryStationDao();
        Station station = new Station("청구역");
        // when
        final Station saved = dao.save(station);
        // then
        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("중복된 이름을 저장하는 경우 예외를 던진다.")
    public void save_throwsExceptionWithDuplicatedName() {
        // given
        StationDao dao = new MemoryStationDao();
        // when
        dao.save(new Station("청구역"));
        // then
        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> dao.save(new Station("청구역")));
    }

    @Test
    @DisplayName("역 목록을 불러온다.")
    public void findAll() {
        // given
        StationDao dao = new MemoryStationDao();
        // when
        final List<Station> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(0);
    }

    @Test
    @DisplayName("역을 하나 추가한 뒤, 역 목록을 불러온다.")
    public void findAll_afterSaveOneStation() {
        // given
        StationDao dao = new MemoryStationDao();
        dao.save(new Station(STATION_NAME));
        // when
        final List<Station> stations = dao.findAll();
        // then
        assertThat(stations).hasSize(1);
    }

    @Test
    @DisplayName("ID값으로 역을 삭제한다.")
    public void deleteById() {
        // given
        StationDao dao = new MemoryStationDao();
        final Station saved = dao.save(new Station(STATION_NAME));
        // when
        final Long id = saved.getId();
        // then
        assertThatNoException().isThrownBy(() -> dao.deleteById(id));
    }

    @Test
    @DisplayName("존재하지 않는 역을 삭제할 수 없다.")
    public void deleteById_doesNotExist() {
        // given & when
        StationDao dao = new MemoryStationDao();
        // then
        assertThatThrownBy(() -> dao.deleteById(1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("삭제하고자 하는 역이 존재하지 않습니다.");
    }
}