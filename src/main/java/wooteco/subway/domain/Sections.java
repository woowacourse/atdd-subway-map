package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

	private final List<Section> values;

	public Sections(List<Section> values) {
		this.values = values;
	}

	public List<Station> getStations() {
		return values.stream()
			.flatMap(section -> section.getStations().stream())
			.collect(Collectors.toList());
	}

	public List<Section> getValues() {
		return new ArrayList<>(values);
	}
}
