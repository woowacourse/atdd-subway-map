package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Construction {

    private static final int MIN_SECTION_COUNT = 1;
    private static final int SECTION_COUNT_DONT_NEED_TO_CREATE = 1;
    private final Line line;
    private final List<Section> sectionsToCreate;
    private final List<Section> sectionsToRemove;

    public Construction(Line line) {
        this.line = line;
        sectionsToCreate = new ArrayList<>();
        sectionsToRemove = new ArrayList<>();
    }

    public void createSection(Section section) {
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
        return section.getUpStation().equals(line.lastStation())
            || section.getDownStation().equals(line.firstStation());
    }

    private void insertSectionWhenNotEndSectionInsertion(Section section) {
        Section sectionToConstruct = sectionToConstruct(section);
        sectionsToRemove.add(sectionToConstruct);
        registerSections(section, sectionToConstruct);
    }

    private Section sectionToConstruct(Section sectionToInsert) {
        return line.sections().stream()
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
        sectionsToCreate
            .add(new Section(section.getDownStation(), sectionToConstruct.getDownStation(),
                new Distance(
                    sectionToConstruct.getDistance().value() - section.getDistance().value())));
    }

    private void registerSectionsToUpdateWhenSameDownStation(Section section,
        Section sectionToConstruct) {
        sectionsToCreate.add(new Section(sectionToConstruct.getUpStation(), section.getUpStation(),
            new Distance(
                sectionToConstruct.getDistance().value() - section.getDistance().value())));
        sectionsToCreate.add(section);
    }

    public void deleteSectionsByStation(Station station) {
        validateToConstruct();
        validateNumberOfSections();
        validateToHasStation(station);
        List<Section> sectionsWithStation = line.sectionsWithStation(station);
        sectionsToRemove.addAll(sectionsWithStation);
        if (sectionsWithStation.size() > SECTION_COUNT_DONT_NEED_TO_CREATE) {
            addSectionsToCreateAfterRemoveSection();
        }
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

    private void addSectionsToCreateAfterRemoveSection() {
        Sections sections = new Sections(new HashSet<>(sectionsToRemove));
        Section newSection = new Section(sections.firstStation(), sections.lastStation(), sections.totalDistance());
        sectionsToCreate.add(newSection);
    }

    public Line line() {
        return line;
    }

    public List<Section> sectionsToCreate() {
        return new ArrayList<>(sectionsToCreate);
    }

    public List<Section> sectionsToRemove() {
        return new ArrayList<>(sectionsToRemove);
    }
}
