package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

	private final Station station1 = new Station(1L, "고대역");
	private final Station station2 = new Station(2L, "강남역");
	private final Station station3 = new Station(3L, "역삼역");
	private final Station station4 = new Station(4L, "선릉역");
	private final Station station5 = new Station(5L, "삼성역");

	private final Section section1 = new Section(station1, station2, 10);
	private final Section section2 = new Section(station2, station3, 10);
	private final Section section3 = new Section(station3, station4, 10);
	private final Section section4 = new Section(station4, station5, 10);

	@DisplayName("상행종점부터 하행종점까지 정렬하여 출력한다.")
	@Test
	void orderStations() {
		List<Section> datas = new ArrayList<>();
		datas.add(section1);
		datas.add(section2);
		datas.add(section3);
		datas.add(section4);
		Collections.shuffle(datas);

		Sections sections = new Sections(datas);
		List<Station> stations = sections.sortStations();
		assertThat(stations).map(Station::getId)
			.containsExactly(1L, 2L, 3L, 4L, 5L);
	}
}