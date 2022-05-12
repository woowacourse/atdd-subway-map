package wooteco.subway.domain;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sections {

	private final List<Section> values;

	public Sections(List<Section> values) {
		this.values = new LinkedList<>(values);
	}

	public void add(Section section) {
		List<Section> matchSections = findMatchStations(section);
		validateSectionAddable(section, matchSections);
		values.add(section);

		findSectionIfUpStationMatch(section, matchSections).ifPresentOrElse(
			matchSection -> updateSection(section, matchSection),
			() -> findSectionIfDownStationMatch(section, matchSections)
				.ifPresent(matchSection -> updateSection(section, matchSection))
		);
	}

	private List<Section> findMatchStations(Section section) {
		return values.stream()
			.filter(each -> each.hasAnySameStation(section))
			.collect(toList());
	}

	private void validateSectionAddable(Section section, List<Section> matchSections) {
		if (matchSections.isEmpty()) {
			throw new IllegalArgumentException("등록할 구간의 상행역과 하행역이 노선에 존재하지 않습니다.");
		}
		if (section.isIncludedIn(matchSections)) {
			throw new IllegalArgumentException("상행역과 하행역 둘 다 이미 노선에 존재합니다.");
		}
	}

	private Optional<Section> findSectionIfUpStationMatch(Section section, List<Section> matchSections) {
		return findSectionByCondition(matchSections, each -> each.hasSameUpStation(section));
	}

	private void updateSection(Section section, Section existSection) {
		Section updatedSection = existSection.dividedBy(section);
		values.remove(existSection);
		values.add(updatedSection);
	}

	private Optional<Section> findSectionIfDownStationMatch(Section section, List<Section> matchSections) {
		return findSectionByCondition(matchSections, each -> each.hasSameDownStation(section));
	}

	private Optional<Section> findSectionByCondition(List<Section> matchSections, Predicate<Section> condition) {
		return matchSections.stream()
			.filter(condition)
			.findAny();
	}

	public List<Station> sortStations() {
		LinkedList<Station> sortedStations = new LinkedList<>();
		Section section = values.get(0);

		fillDownStream(sortedStations, section);
		fillUpStream(sortedStations, section);

		return sortedStations;
	}

	private void fillDownStream(LinkedList<Station> sortedStations, Section section) {
		Map<Station, Station> toDownStations = toStationMap(
			Section::getUpStation, Section::getDownStation);
		addDownStream(sortedStations, toDownStations, section.getDownStation());
	}

	private void fillUpStream(LinkedList<Station> sortedStations, Section section) {
		Map<Station, Station> toUpStations = toStationMap(
			Section::getDownStation, Section::getUpStation);
		addUpStream(sortedStations, toUpStations, section.getUpStation());
	}

	private Map<Station, Station> toStationMap(
		Function<Section, Station> keyMapper,
		Function<Section, Station> valueMapper) {
		return values.stream()
			.collect(toMap(keyMapper, valueMapper));
	}

	private void addDownStream(LinkedList<Station> sortedStations, Map<Station, Station> stations, Station station) {
		sortedStations.addLast(station);
		Optional.ofNullable(stations.get(station))
			.ifPresent(value -> addDownStream(sortedStations, stations, value));
	}

	private void addUpStream(LinkedList<Station> sortedStations, Map<Station, Station> stations, Station station) {
		sortedStations.addFirst(station);
		Optional.ofNullable(stations.get(station))
			.ifPresent(value -> addUpStream(sortedStations, stations, value));
	}

	public void executeEach(Consumer<Section> consumer) {
		values.forEach(consumer);
	}

	public void deleteByStation(Long stationId) {
		List<Section> sections = values.stream()
			.filter(section -> section.matchAnyStation(stationId))
			.collect(toList());
		validateSectionExist(sections);
		sections.forEach(values::remove);
		Sections resultSections = new Sections(sections);
		addUpdatedSection(resultSections);
	}

	private void validateSectionExist(List<Section> deletedSections) {
		if (deletedSections.isEmpty()) {
			throw new IllegalArgumentException("삭제하려는 역 구간이 없습니다.");
		}
	}

	private void addUpdatedSection(Sections resultSections) {
		Section newSection = resultSections.sum();
		if (isNotExist(newSection)) {
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

	public boolean isNotExist(Section section) {
		return values.stream()
			.noneMatch(
				each -> each.hasSameUpStation(section)
					&& each.hasSameDownStation(section));
	}
}
