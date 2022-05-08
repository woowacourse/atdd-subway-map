package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

	private final List<Section> values;

	public Sections(List<Section> values) {
		this.values = new LinkedList<>(values);
	}

	public Optional<Section> add(Section section) {
		List<Section> matchSections = values.stream()
			.filter(each -> each.hasAnySameStation(section))
			.collect(Collectors.toList());

		if (matchSections.isEmpty()) {
			throw new IllegalArgumentException("등록할 구간의 상행역과 하행역이 노선에 존재하지 않습니다.");
		}

		Optional<Section> sameUpStationSection = matchSections.stream()
			.filter(each -> each.hasSameUpStation(section))
			.findAny();

		Optional<Section> sameDownStationSection = matchSections.stream()
			.filter(each -> each.hasSameDownStation(section))
			.findAny();

		if (sameUpStationSection.isPresent() && sameDownStationSection.isPresent()) {
			throw new IllegalArgumentException("상행역과 하행역 둘 다 이미 노선에 존재합니다.");
		}

		Section updatedSection = null;

		if (sameUpStationSection.isPresent()) {
			Section existSection = sameUpStationSection.get();
			updatedSection = existSection.dividedBy(section);
			values.remove(existSection);
			values.add(updatedSection);
		}

		if (sameDownStationSection.isPresent()) {
			Section existSection = sameDownStationSection.get();
			updatedSection = existSection.dividedBy(section);
			values.remove(existSection);
			values.add(updatedSection);
		}

		values.add(section);
		return Optional.ofNullable(updatedSection);
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
