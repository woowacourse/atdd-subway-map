package wooteco.subway.section.domain;

import wooteco.subway.exception.sectionsexception.SectionDeleteException;
import wooteco.subway.exception.sectionsexception.SectionUpdateException;
import wooteco.subway.exception.notfoundexception.NotFoundSectionException;
import wooteco.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sortSections(sections);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> sortSections(List<Section> sections) {
        Section topSection = findTopSection(sections);
        return sorting(sections, topSection);
    }

    private Section findTopSection(List<Section> sections) {
        List<Station> downStationIds = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        // todo 인덴트 2
        for (Section section : sections) {
            if (downStationIds.stream()
                    .noneMatch(section::isUpStation)) {
                return section;
            }
        }

        throw new IllegalStateException("잘못된 구간 정보 입니다.");
    }

    private List<Section> sorting(List<Section> sections, Section topSection) {
        List<Section> sortedSections = new ArrayList<>();

        sortedSections.add(topSection);

        int size = sections.size();
        Station curDownStation = topSection.getDownStation();

        // todo 인덴트 2
        for (int i = 0; i < size - 1; i++) {
            for (Section section : sections) {
                if (section.isUpStation(curDownStation)) {
                    sortedSections.add(section);
                    curDownStation = section.getDownStation();
                    break;
                }
            }
        }

        return sortedSections;
    }

    public List<Long> getStationsId() {
        List<Long> stationsId = sections.stream()
                .map(Section::getUpStation)
                .map(Station::getId)
                .collect(Collectors.toList());

        stationsId.add(getLastSection().getDownStation().getId());

        return stationsId;
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public Section addSection(Section section) {
        Section targetSection = validateAddSection(section);

        if (isBetweenAddCase(section, targetSection)) {
            return getUpdateSection(section, targetSection);
        }
        return section;
    }

    private Section getUpdateSection(Section section, Section targetSection) {
        int updateSectionDistance = targetSection.getDistance() - section.getDistance();
        if (section.isUpStation(targetSection.getUpStation())) {
            return new Section(targetSection.getId(), targetSection.getLineId(), section.getDownStation(), targetSection.getDownStation(), updateSectionDistance);
        }
        return new Section(targetSection.getId(), targetSection.getLineId(), targetSection.getUpStation(), section.getUpStation(), updateSectionDistance);
    }

    private Section validateAddSection(Section section) {
        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();

        if (sections.stream()
                .anyMatch(existSection ->
                        (existSection.isUpStation(upStation) && existSection.isDownStation(downStation)) ||
                                (existSection.isUpStation(downStation) && existSection.isDownStation(upStation)))) {
            throw new SectionUpdateException("중복된 구간입니다.");
        }

        if (sections.stream()
                .noneMatch(existSection ->
                        (existSection.isUpStation(upStation) || existSection.isDownStation(downStation)) ||
                                (existSection.isUpStation(downStation) || existSection.isDownStation(upStation)))) {
            throw new SectionUpdateException("상행역 또는 하행역이 포함되어야 합니다.");
        }

        Section targetSection = targetSection(upStation, downStation);

        if (isBetweenAddCase(section, targetSection)) {
            validateDistance(targetSection, section);
        }

        return targetSection;
    }

    private Section targetSection(Station upStation, Station downStation) {
        if (isEndStation(upStation) || isEndStation(downStation)) {
            return isTargetSectionExistEnd(upStation, downStation);
        }

        return isTargetSectionExistBetween(upStation, downStation);
    }

    private Section isTargetSectionExistBetween(Station upStation, Station downStation) {
        List<Section> sections = this.sections.stream()
                .filter(existSection ->
                        (existSection.isUpStation(upStation) || existSection.isDownStation(downStation) ||
                                (existSection.isUpStation(downStation) || existSection.isDownStation(upStation))))
                .collect(Collectors.toList());

        Station fixedStation = findFixedId(sections, upStation, downStation);
        if (fixedStation.equals(upStation)) {
            return sections.stream()
                    .filter(section -> section.isUpStation(fixedStation))
                    .findFirst()
                    .orElseThrow(NotFoundSectionException::new);
        }
        return sections.stream()
                .filter(section -> section.isDownStation(fixedStation))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
    }

    private Section isTargetSectionExistEnd(Station upStation, Station downStation) {
        return sections.stream()
                .filter(existSection ->
                        (existSection.isUpStation(upStation) || existSection.isDownStation(downStation) ||
                                (existSection.isUpStation(downStation) || existSection.isDownStation(upStation))))
                .findFirst().orElseThrow(() -> new IllegalStateException("잘못된 구간 정보입니다."));
    }

    private Station findFixedId(List<Section> sections, Station upStation, Station downStation) {
        Section firstSection = sections.get(0);
        if (firstSection.isUpStation(upStation) || firstSection.isDownStation(upStation)) {
            return upStation;
        }
        return downStation;
    }

    private boolean isBetweenAddCase(Section section, Section targetSection) {
        return targetSection.isUpStation(section.getUpStation()) || targetSection.isDownStation(section.getDownStation());
    }

    private void validateDistance(Section targetSection, Section section) {
        if (targetSection.compareDistance(section.getDistance())) {
            throw new SectionUpdateException("추가할 구간의 거리는 기존 구간 거리보다 작아야 합니다.");
        }
    }

    public Optional<Section> findUpdateSectionAfterDelete(Long lineId, Station station) {
        validateDeleteSection();
        if (isEndStation(station)) {
            deleteEndStation(station);
            return Optional.empty();
        }

        Section upSection = sections.stream()
                .filter(section -> section.isDownStation(station))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
        Section downSection = sections.stream()
                .filter(section -> section.isUpStation(station))
                .findFirst()
                .orElseThrow(NotFoundSectionException::new);
        int updateSectionDistance = upSection.getDistance() + downSection.getDistance();

        Section updateSection = new Section(lineId, upSection.getUpStation(), downSection.getDownStation(), updateSectionDistance);

        deleteSection(upSection, downSection, updateSection);

        return Optional.of(updateSection);
    }

    private void validateDeleteSection() {
        if (sections.size() == 1) {
            throw new SectionDeleteException("구간이 하나인 노선에서 마지막 구간을 제거할 수 없습니다.");
        }
    }

    private void deleteSection(Section upSection, Section downSection, Section updateSection) {
        sections.remove(upSection);
        sections.remove(downSection);
        sections.add(updateSection);
    }

    private boolean isEndStation(Station station) {
        Section topSection = sections.get(0);
        Section bottomSection = sections.get(sections.size() - 1);

        return topSection.isUpStation(station) || bottomSection.isDownStation(station);
    }

    private void deleteEndStation(Station station) {
        Section topSection = sections.get(0);
        Section bottomSection = getLastSection();

        if (topSection.isUpStation(station)) {
            sections.remove(topSection);
            return;
        }

        sections.remove(bottomSection);
    }
}
