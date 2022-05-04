package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.Station;

class StationDaoTest {

    @Test
    @DisplayName("역을 저장한다.")
    public void save() {
        // given
        StationDao dao = new StationDao();
        Station station = new Station("청구역");
        // when
        final Station saved = dao.save(station);
        // then
        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("역 목록을 불러온다.")
    public void findAll() {
        // given
        StationDao dao = new StationDao();
        // when
        final List<Station> stations = dao.findAll();
        // then
        assertThat(stations.size()).isEqualTo(0);
    }

}