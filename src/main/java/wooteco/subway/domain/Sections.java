package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        validateEmpty(sections);
        this.sections = sortSections(sections);
    }

    private void validateEmpty(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("구간이 존재하지 않습니다.");
        }
    }

    private List<Section> sortSections(List<Section> sections) {
        List<Section> sortedSections = new ArrayList<>();
        Long topStationId = getTopStationId(sections);

        for (int i = 0; i < sections.size(); i++) {
            Section nextSection = getNextSection(sections, topStationId);
            sortedSections.add(nextSection);
            topStationId = nextSection.getDownStationId();
        }

        return sortedSections;
    }

    private Long getTopStationId(List<Section> sections) {
        Set<Long> downStationIds = getDownStationIds(sections);
        return sections.stream()
                .map(Section::getUpStationId)
                .filter(sectionId -> !downStationIds.contains(sectionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("상행종점을 찾을 수 없습니다."));
    }

    private Set<Long> getDownStationIds(List<Section> sections) {
        return sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toSet());
    }

    private Section getNextSection(List<Section> sections, Long topStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(topStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 역을 찾을 수 없습니다."));
    }

    public void validateSectionInLine(Section newSection) {
        List<Long> stationIds = findStationIds();
        boolean existUpStation = stationIds.contains(newSection.getUpStationId());
        boolean existDownStation = stationIds.contains(newSection.getDownStationId());

        validateBothStationsIncludeInLine(existUpStation, existDownStation);
        validateBothStationsExcludeInLine(existUpStation, existDownStation);
    }

    private void validateBothStationsIncludeInLine(boolean existUpStation, boolean existDownStation) {
        if (!(existUpStation || existDownStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 노선에 포함되어있지 않습니다.");
        }
    }

    private void validateBothStationsExcludeInLine(boolean existUpStation, boolean existDownStation) {
        if (existUpStation && existDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 모두 노선에 포함되어 있습니다.");
        }
    }

    public void validateSectionDistance(Section newSection) {
        if (newSection.getDistance() >= getExistSection(newSection).getDistance()) {
            throw new IllegalArgumentException("구간의 길이는 기존 역 사이의 길이보다 작아야합니다.");
        }
    }

    private Section getExistSection(Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(newSection.getUpStationId())
                        || section.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 구간입니다."));
    }

    public Section getUpdatedSectionForSave(Section newSection) {
        List<Long> stationIds = findStationIds();
        Section existSection = getExistSection(newSection);
        int newDistance = existSection.getDistance() - newSection.getDistance();

        if (stationIds.contains(newSection.getDownStationId())) {
            return new Section(existSection.getId(), existSection.getLineId(), existSection.getUpStationId(),
                    newSection.getUpStationId(), newDistance);
        }

        return new Section(existSection.getId(), existSection.getLineId(), newSection.getDownStationId(),
                existSection.getDownStationId(), newDistance);
    }

    public boolean isRequireUpdateForSave(Section newSection) {
        return !(isInsertTop(newSection) || isInsertBottom(newSection));
    }

    private boolean isInsertTop(Section newSection) {
        Long upStationId = newSection.getUpStationId();
        Long bottomStationId = sections.get(sections.size() - 1).getDownStationId();
        return upStationId.equals(bottomStationId);
    }

    private boolean isInsertBottom(Section newSection) {
        Long downStationId = newSection.getDownStationId();
        Long topStationId = sections.get(0).getUpStationId();
        return downStationId.equals(topStationId);
    }

    public void validateDelete(Long stationId) {
        validateNotExistStation(stationId);
        validateLastSection();
    }

    private void validateNotExistStation(Long stationId) {
        if (!findStationIds().contains(stationId)) {
            throw new IllegalArgumentException("해당 노선에 등록되지 않은 역입니다.");
        }
    }

    private void validateLastSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 하나인 노선에서 마지막 구간을 삭제할 수 없습니다.");
        }
    }

    public List<Long> findStationIds() {
        List<Long> stationIds = sections.stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toList());
        stationIds.add(0, sections.get(0).getUpStationId());
        return stationIds;
    }
}
