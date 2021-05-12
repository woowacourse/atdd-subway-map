package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Construction {

    private static final int MIN_SECTION_COUNT = 1;
    private static final int SECTION_COUNT_DONT_NEED_TO_CREATE = 1;

    private final Line line;

    public Construction(Line line) {
        this.line = line;
    }

    public Estimate createSection(Section section) {
        List<Section> sectionsToCreate = new ArrayList<>();
        List<Section> sectionsToRemove = new ArrayList<>();
        if (isEndSectionInsertion(section)) {
            sectionsToCreate.add(section);
            return new Estimate(sectionsToCreate, sectionsToRemove);
        }
        insertSectionWhenNotEndSectionInsertion(section, sectionsToCreate, sectionsToRemove);
        return new Estimate(sectionsToCreate, sectionsToRemove);
    }

    private boolean isEndSectionInsertion(Section section) {
        return section.getUpStation().equals(line.lastStation())
            || section.getDownStation().equals(line.firstStation());
    }

    private void insertSectionWhenNotEndSectionInsertion(Section section,
        List<Section> sectionsToCreate, List<Section> sectionsToRemove) {
        Section sectionToConstruct = sectionToConstruct(section);
        sectionsToRemove.add(sectionToConstruct);
        registerSections(section, sectionToConstruct, sectionsToCreate);
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return line.sections().stream()
            .filter(section -> section.hasOnlyOneSameStation(sectionToInsert))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("추가할 수 없는 구간입니다."));
    }

    private void registerSections(Section section, Section sectionToConstruct,
        List<Section> sectionsToCreate) {
        Station sameStation = sectionToConstruct.sameStation(section);
        if (section.getUpStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameUpStation(section, sectionToConstruct,
                sectionsToCreate);
        }
        if (section.getDownStation().equals(sameStation)) {
            registerSectionsToUpdateWhenSameDownStation(section, sectionToConstruct,
                sectionsToCreate);
        }
    }

    private void registerSectionsToUpdateWhenSameUpStation(Section section,
        Section sectionToConstruct, List<Section> sectionsToCreate) {
        sectionsToCreate.add(section);
        sectionsToCreate
            .add(new Section(section.getDownStation(), sectionToConstruct.getDownStation(),
                new Distance(
                    sectionToConstruct.getDistance().value() - section.getDistance().value())));
    }

    private void registerSectionsToUpdateWhenSameDownStation(Section section,
        Section sectionToConstruct, List<Section> sectionsToCreate) {
        sectionsToCreate.add(new Section(sectionToConstruct.getUpStation(), section.getUpStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
        sectionsToCreate.add(section);
    }

    public Estimate deleteSectionsByStation(Station station) {
        validateNumberOfSections();
        validateToHasStation(station);
        List<Section> sectionsToCreate = new ArrayList<>();
        List<Section> sectionsToRemove = line.sectionsWithStation(station);
        if (sectionsToRemove.size() > SECTION_COUNT_DONT_NEED_TO_CREATE) {
            addSectionsToCreateAfterRemoveSection(sectionsToCreate, sectionsToRemove);
        }
        return new Estimate(sectionsToCreate, sectionsToRemove);
    }

    private void validateToHasStation(Station station) {
        if (line.hasNotStation(station)) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
    }

    private void validateNumberOfSections() {
        if (line.sectionCount() <= MIN_SECTION_COUNT) {
            throw new IllegalStateException("구간이 하나 남은 경우 삭제할 수 없습니다.");
        }
    }

    private void addSectionsToCreateAfterRemoveSection(List<Section> sectionsToCreate, List<Section> sectionsToRemove) {
        Sections sections = new Sections(new HashSet<>(sectionsToRemove));
        Section newSection = new Section(sections.firstStation(), sections.lastStation(),
            sections.totalDistance());
        sectionsToCreate.add(newSection);
    }

    public Line line() {
        return line;
    }
}
