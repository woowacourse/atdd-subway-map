package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Sections {

	private final List<Section> values;

	public Sections(List<Section> values) {
		this.values = new LinkedList<>(values);
	}

	public Optional<Section> add(Section section) {
		List<Section> matchSections = findMatchStations(section);
		validateMatchAnyStation(matchSections);
		validateNotHasBothStations(section, matchSections);
		values.add(section);

		return findSectionIfUpStationMatch(section, matchSections)
			.map(value -> updateSection(section, value))
			.orElseGet(() -> findSectionIfDownStationMatch(section, matchSections));
	}

	private List<Section> findMatchStations(Section section) {
		return values.stream()
			.filter(each -> each.hasAnySameStation(section))
			.collect(Collectors.toList());
	}

	private void validateMatchAnyStation(List<Section> matchSections) {
		if (matchSections.isEmpty()) {
			throw new IllegalArgumentException("등록할 구간의 상행역과 하행역이 노선에 존재하지 않습니다.");
		}
	}

	private void validateNotHasBothStations(Section section, List<Section> matchSections) {
		if (section.isIncludedIn(matchSections)) {
			throw new IllegalArgumentException("상행역과 하행역 둘 다 이미 노선에 존재합니다.");
		}
	}

	private Optional<Section> findSectionIfUpStationMatch(Section section, List<Section> matchSections) {
		return findSectionByCondition(matchSections, each -> each.hasSameUpStation(section));
	}

	private Optional<Section> updateSection(Section section, Section existSection) {
		Section updatedSection = existSection.dividedBy(section);
		values.remove(existSection);
		values.add(updatedSection);
		return Optional.of(updatedSection);
	}

	private Optional<Section> findSectionIfDownStationMatch(Section section, List<Section> matchSections) {
		return findSectionByCondition(matchSections, each -> each.hasSameDownStation(section))
			.flatMap(value -> updateSection(section, value));
	}

	private Optional<Section> findSectionByCondition(List<Section> matchSections, Predicate<Section> condition) {
		return matchSections.stream()
			.filter(condition)
			.findAny();
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

	public void executeEach(Consumer<Section> consumer) {
		values.forEach(consumer);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public Sections deleteByStation(Long stationId) {
		List<Section> sections = values.stream()
			.filter(section -> section.matchAnyStation(stationId))
			.collect(Collectors.toList());
		sections.forEach(values::remove);
		Sections resultSections = new Sections(sections);
		addUpdatedSection(resultSections);
		return resultSections;
	}

	private void addUpdatedSection(Sections resultSections) {
		Section newSection = resultSections.sum();
		if (!isExist(newSection)) {
			values.add(newSection);
		}
	}

	public int size() {
		return values.size();
	}

	public List<Section> getValues() {
		return new ArrayList<>(values);
	}

	public Section sum() {
		List<Station> stations = sortStations();
		int size = stations.size();
		int sumDistance = addAllDistance();
		return new Section(stations.get(0), stations.get(size - 1), sumDistance);
	}

	private int addAllDistance() {
		return values.stream()
			.mapToInt(Section::getDistance)
			.sum();
	}

	public boolean isExist(Section section) {
		return values.stream()
			.anyMatch(
				each -> each.hasSameUpStation(section)
					&& each.hasSameDownStation(section));
	}
}
