package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sections {

	private final List<Section> values;

	public Sections(List<Section> values) {
		this.values = values;
	}

	public List<Station> sortStations() {
		LinkedList<Station> sortedStations = new LinkedList<>();
		Section section = values.get(0);
		sortUpStream(sortedStations, section);
		sortDownStream(sortedStations, section);
		return sortedStations;
	}

	private void sortUpStream(LinkedList<Station> sortedStations, Section pickedSection) {
		Station upStation = pickedSection.getUpStation();
		sortedStations.addFirst(upStation);
		values.stream()
			.filter(section -> section.isDownStation(upStation))
			.findAny()
			.ifPresent(section -> sortUpStream(sortedStations, section));
	}

	private void sortDownStream(LinkedList<Station> sortedStations, Section pickedSection) {
		Station downStation = pickedSection.getDownStation();
		sortedStations.addLast(downStation);
		values.stream()
			.filter(section -> section.isUpStation(downStation))
			.findAny()
			.ifPresent(section -> sortDownStream(sortedStations, section));
	}

	public List<Section> getValues() {
		return new ArrayList<>(values);
	}
}
