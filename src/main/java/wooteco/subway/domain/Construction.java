package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Construction {

    private static final int MIN_SECTION_COUNT = 1;
    private final Line line;
    private final Set<Section> sections;
    private final List<Section> sectionsToCreate;
    private final List<Section> sectionsToRemove;

    public Construction(Set<Section> sections, Line line) {
        this.line = line;
        this.sections = new HashSet<>(sections);
        this.sectionsToCreate = new ArrayList<>();
        this.sectionsToRemove = new ArrayList<>();
    }

    public void insertSection(Section section) {
        validateToConstruct();
        if (isEndSectionInsertion(section)) {
            sectionsToCreate.add(section);
            return;
        }
        insertSectionWhenNotEndSectionInsertion(section);
    }

    private void validateToConstruct() {
        if (!sectionsToCreate.isEmpty() || !sectionsToRemove.isEmpty()) {
            throw new IllegalStateException("이미 구간을 수정하였습니다.");
        }
    }

    private boolean isEndSectionInsertion(Section section) {
        Section firstSection = firstSection();
        Section lastSection = lastSection();

        return firstSection.getUpStation().equals(section.getDownStation())
            || lastSection.getDownStation().equals(section.getUpStation());
    }

    private void insertSectionWhenNotEndSectionInsertion(Section section) {
        Section sectionToConstruct = sectionToConstruct(section);
        sectionsToRemove.add(sectionToConstruct);
        registerSections(section, sectionToConstruct);
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return sections.stream()
            .filter(section -> section.hasOnlyOneSameStation(sectionToInsert))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("추가할 수 없는 구간입니다."));
    }

    private void registerSections(Section section, Section sectionToConstruct) {
        Station sameStation = sectionToConstruct.sameStation(section);
        if (section.getUpStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameUpStation(section, sectionToConstruct);
        }
        if (section.getDownStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameDownStation(section, sectionToConstruct);
        }
    }

    private void registerSectionsToUpdateWhenSameUpStation(Section section,
        Section sectionToConstruct) {
        sectionsToCreate.add(section);
        sectionsToCreate.add(new Section(line,
            section.getDownStation(),
            sectionToConstruct.getDownStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
    }

    private void registerSectionsToUpdateWhenSameDownStation(Section section,
        Section sectionToConstruct) {
        sectionsToCreate.add(new Section(line,
            sectionToConstruct.getUpStation(),
            section.getUpStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
        sectionsToCreate.add(section);
    }

    private Section firstSection() {
        return sections.stream()
            .filter(section -> sections.stream()
                .noneMatch(section1 -> section.getUpStation().equals(section1.getDownStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("첫 번째 구간이 존재하지 않습니다."));
    }

    private Section lastSection() {
        return sections.stream()
            .filter(section -> sections.stream()
                .noneMatch(section1 -> section.getDownStation().equals(section1.getUpStation())))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("마지막 구간이 존재하지 않습니다."));
    }

    public void deleteSectionsByStation(Station station) {
        validateToConstruct();
        validateNumberOfSections();
        validateToHasStation(station);
        sectionsToRemove.addAll(sectionsWithStation(station));
        if (isNotEndStationDeletion(station)) {
            addSectionsToCreateAfterRemoveSection();
        }
    }

    private void validateNumberOfSections() {
        if (sections.size() <= MIN_SECTION_COUNT) {
            throw new IllegalStateException("구간이 하나 남은 경우 삭제할 수 없습니다.");
        }
    }

    private void validateToHasStation(Station station) {
        sections.stream()
            .filter(section -> section.hasStation(station))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    private void addSectionsToCreateAfterRemoveSection() {
        Sections affectedSections = new Sections(new HashSet<>(sectionsToRemove));
        Distance newDistance = affectedSections.totalDistance();
        List<Station> affectedStationPath = affectedSections.pathByLine(line);
        Station upStationToCreate = affectedStationPath.get(0);
        Station downStationToCreate = affectedStationPath.get(affectedStationPath.size() - 1);
        sectionsToCreate.add(new Section(line, upStationToCreate, downStationToCreate, newDistance));
    }

    private boolean isNotEndStationDeletion(Station station) {
        Station firstStation = firstSection().getUpStation();
        Station lastStation = lastSection().getDownStation();

        return !(firstStation.equals(station) || lastStation.equals(station));
    }

    private List<Section> sectionsWithStation (Station station) {
        return sections.stream()
            .filter(section -> section.hasStation(station))
            .collect(Collectors.toList());
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> getSectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }
}
