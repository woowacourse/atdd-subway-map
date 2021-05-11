package wooteco.subway.domain.line.section;

import wooteco.subway.exception.line.AlreadyExistingUpAndDownStationsException;
import wooteco.subway.exception.line.ConnectableStationNotFoundException;
import wooteco.subway.exception.line.SectionDeleteException;
import wooteco.subway.exception.line.SectionLengthException;
import wooteco.subway.domain.line.value.line.LineId;
import wooteco.subway.domain.line.value.section.Distance;
import wooteco.subway.domain.line.value.section.SectionId;
import wooteco.subway.domain.station.value.StationId;

import java.util.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections empty() {
        return new Sections(Collections.emptyList());
    }

    public void add(Section section) {
        validateThatConnectableStationIsExisting(section);
        validateThatSectionIsAlreadyExisting(section);

        if (registerTerminal(section)) return;
        preventForkedRoad(section);
    }

    private boolean registerTerminal(Section section) {
        Deque<Long> stationIds = new ArrayDeque<>(getStationIds());

        if (Objects.equals(stationIds.getFirst(), section.getDownStationId()) ||
                Objects.equals(stationIds.getLast(), section.getUpStationId())) {
            sections.add(section);
            return true;
        }

        return false;
    }

    private void validateThatSectionDistanceIsLowerThenExistingSection(Section section, Section selectedSection) {
        if (selectedSection.getDistance() <= section.getDistance()) {
            throw new SectionLengthException();
        }
    }

    private void preventForkedRoad(Section section) {
        if (caseWhereDownStationExists(section)) return;
        if (caseWhereUpStationExists(section)) return;
    }

    //todo reduce duplicated code
    private boolean caseWhereDownStationExists(Section section) {
        Optional<Section> wrappedSelectedSection = getSectionThatHasSameDownStationByFromSections(section);
        if (!wrappedSelectedSection.isPresent()) {
            return false;
        }

        Section selectedSection = wrappedSelectedSection.get();

        validateThatSectionDistanceIsLowerThenExistingSection(section, selectedSection);

        Section newSection = new Section(
                new SectionId(selectedSection.getId()),
                new LineId(selectedSection.getLineId()),
                new StationId(selectedSection.getUpStationId()),
                new StationId(section.getUpStationId()),
                new Distance(selectedSection.getDistance() - section.getDistance())
        );

        sections.remove(selectedSection);
        sections.add(section);
        sections.add(newSection);

        return true;
    }

    private boolean caseWhereUpStationExists(Section section) {
        Optional<Section> wrappedSelectedSection = getSectionThatHasSameUpStationByFromSections(section);
        if (!wrappedSelectedSection.isPresent()) {
            return false;
        }

        Section selectedSection = wrappedSelectedSection.get();

        validateThatSectionDistanceIsLowerThenExistingSection(section, selectedSection);

        Section newSection = new Section(
                new SectionId(selectedSection.getId()),
                new LineId(selectedSection.getLineId()),
                new StationId(section.getDownStationId()),
                new StationId(selectedSection.getDownStationId()),
                new Distance(selectedSection.getDistance() - section.getDistance())
        );

        sections.remove(selectedSection);
        sections.add(section);
        sections.add(newSection);

        return true;
    }

    private Optional<Section> getSectionThatHasSameUpStationByFromSections(Section sourceSection) {
        return sections.stream()
                .filter(
                        section -> Objects.equals(sourceSection.getUpStationId(), section.getUpStationId())
                )
                .findAny();
    }

    private Optional<Section> getSectionThatHasSameDownStationByFromSections(Section sourceSection) {
        return sections.stream()
                .filter(
                        section -> Objects.equals(sourceSection.getDownStationId(), section.getDownStationId())
                )
                .findAny();
    }

    private void validateThatSectionIsAlreadyExisting(Section newSection) {
        List<Long> stationIds = getStationIds();
        int indexOfUpStationId = stationIds.indexOf(newSection.getUpStationId());
        int indexOfDownStationId = stationIds.indexOf(newSection.getDownStationId());


        if (indexOfUpStationId != -1 && indexOfDownStationId != -1) {
            throw new AlreadyExistingUpAndDownStationsException();
        }

    }

    private void validateThatConnectableStationIsExisting(Section newSection) {
        List<Long> stationIds = sections.stream()
                .flatMap(section ->
                        Stream.of(
                                section.getUpStationId(),
                                section.getDownStationId()
                        )
                )
                .distinct()
                .collect(toList());

        if (!stationIds.contains(newSection.getDownStationId()) &&
                !stationIds.contains(newSection.getUpStationId())) {
            throw new ConnectableStationNotFoundException();
        }
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public List<Long> getStationIds() {
        if (sections.isEmpty()) return Collections.emptyList();

        long maxStationsId = getMaxStationsId();

        long[] stationIndexesByUpStationId = new long[(int) maxStationsId + 1];
        long[] stationIndexesByDownStationId = new long[(int) maxStationsId + 1];

        initializeStationIdArray(stationIndexesByUpStationId, stationIndexesByDownStationId);

        return sortStationIds(stationIndexesByUpStationId, stationIndexesByDownStationId);
    }

    private List<Long> sortStationIds(long[] stationIndexesByUpStationId, long[] stationIndexesByDownStationId) {
        Deque<Long> stationIds = new ArrayDeque<>();

        Section section = sections.get(0);
        stationIds.addFirst(section.getUpStationId());
        stationIds.addLast(section.getDownStationId());

        long nextId;
        while ((nextId = stationIndexesByDownStationId[stationIds.getFirst().intValue()]) != 0) {
            stationIds.addFirst(nextId);
        }

        while ((nextId = stationIndexesByUpStationId[stationIds.getLast().intValue()]) != 0) {
            stationIds.addLast(nextId);
        }

        return new ArrayList<>(stationIds);
    }

    private void initializeStationIdArray(long[] stationIndexesByUpStationId, long[] stationIndexesByDownStationId) {
        sections.forEach(section -> {
            int upStationId = section.getUpStationId().intValue();
            int downStationId = section.getDownStationId().intValue();

            stationIndexesByUpStationId[upStationId] = downStationId;
            stationIndexesByDownStationId[downStationId] = upStationId;
        });
    }

    private long getMaxStationsId() {
        return sections.stream()
                .flatMapToLong(section -> LongStream.of(
                        section.getUpStationId(),
                        section.getDownStationId()))
                .max()
                .orElse(-1L);
    }

    public void deleteSectionByStationId(Long stationId) {
        if(sections.size() == 1) {
            throw new SectionDeleteException();
        }

        Optional<Section> wrappedUpSection = sections.stream()
                .filter(section -> Objects.equals(section.getUpStationId(), stationId))
                .findAny();

        Optional<Section> wrappedDownSection = sections.stream()
                .filter(section -> Objects.equals(section.getDownStationId(), stationId))
                .findAny();

        if(wrappedDownSection.isPresent() && wrappedUpSection.isPresent()) {
            Section upSection = wrappedDownSection.get();
            Section downSection = wrappedUpSection.get();

            sections.remove(upSection);
            sections.remove(downSection);

            sections.add(new Section(
                    new LineId(upSection.getLineId()),
                    new StationId(upSection.getUpStationId()),
                    new StationId(downSection.getDownStationId()),
                    new Distance(upSection.getDistance() + downSection.getDistance())
            ));
        }

        if(wrappedDownSection.isPresent() && !wrappedUpSection.isPresent()) {
            sections.remove(wrappedDownSection.get());
        }

        if(wrappedUpSection.isPresent() && !wrappedDownSection.isPresent()) {
            sections.remove(wrappedUpSection.get());
        }

    }

}
