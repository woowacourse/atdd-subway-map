package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.domain.Station;

class StationTest {

    @Test
    @DisplayName("이름으로 역 생성")
    void createStationWithName() {
        // given
        String name = "서초역";

        // when
        Station station = new Station(name);

        // then
        assertThat(station).isInstanceOf(Station.class);
        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("아이디와 이름으로 역 생성")
    void createStationWithIdAndName() {
        // given
        long id = 1;
        String name = "서초역";

        // when
        Station station = new Station(id, name);

        // then
        assertThat(station).isInstanceOf(Station.class);
        assertThat(station.getId()).isEqualTo(id);
        assertThat(station.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("아이디, 이름 가져오기")
    void getName() {
        // given
        Station station1 = new Station();

        String givenName2 = "대림역";
        Station station2 = new Station(givenName2);

        long givenId3 = 2;
        String givenName3 = "군자역";
        Station station3 = new Station(givenId3, givenName3);

        // when
        Long id1 = station1.getId();
        String name1 = station1.getName();

        Long id2 = station2.getId();
        String name2 = station2.getName();

        Long id3 = station3.getId();
        String name3 = station3.getName();

        // then
        assertThat(id1).isNull();
        assertThat(name1).isNull();

        assertThat(id2).isNull();
        assertThat(name2).isEqualTo(givenName2);

        assertThat(id3).isEqualTo(givenId3);
        assertThat(name3).isEqualTo(givenName3);
    }
}