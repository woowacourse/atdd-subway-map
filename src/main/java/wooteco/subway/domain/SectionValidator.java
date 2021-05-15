package wooteco.subway.domain;

import wooteco.subway.exception.InvalidRequestException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SectionValidator {

    private final List<Section> allSectionsOnLine;
    private final Section newSection;

    public SectionValidator(final List<Section> allSectionsOnLine, final Section newSection) {
        this.allSectionsOnLine = allSectionsOnLine;
        this.newSection = newSection;
    }

    public void validate() {
        final Optional<Section> sectionByUpStationId = retrieveSection(section -> section.isUpStationIdEquals(newSection));
        final Optional<Section> sectionByDownStationId = retrieveSection(section -> section.isDownStationIdEquals(newSection));

        final boolean canSaveSectionAtUpStationOnLine = sectionByUpStationId.isPresent();
        final boolean canSaveSectionAtDownStationOnLine = sectionByDownStationId.isPresent();

        checkThatSavedSectionHasSection(canSaveSectionAtUpStationOnLine, canSaveSectionAtDownStationOnLine);
        checkDuplicateSection(canSaveSectionAtUpStationOnLine, canSaveSectionAtDownStationOnLine);

        Section oldSection = sectionByUpStationId.orElseGet(sectionByDownStationId::get);
        checkValidDistance(oldSection, newSection);
    }

    private Optional<Section> retrieveSection(final Predicate<Section> sectionPredicate) {
        return allSectionsOnLine.stream().filter(sectionPredicate).findAny();
    }

    private void checkThatSavedSectionHasSection(final boolean existsUpStation, final boolean existsDownStation) {
        if (!(existsUpStation || existsDownStation)) {
            throw new InvalidRequestException("적어도 구간의 하나의 역은 이미 다른 구간에 저장되어 있어야 합니다.");
        }
    }

    private void checkDuplicateSection(final boolean existsUpStation, final boolean existsDownStation) {
        if (existsUpStation && existsDownStation) {
            throw new InvalidRequestException("이미 저장되어 있는 구간입니다.");
        }
    }

    private void checkValidDistance(final Section oldSection, final Section newSection) {
        if (oldSection.getDistance() <= newSection.getDistance()) {
            throw new InvalidRequestException("추가하려는 구간의 길이는 기존 구간의 길이보다 커야 합니다.");
        }
    }
}
