package wooteco.subway.domain;

import java.util.List;
import java.util.Optional;

public class Sections {
    private final List<Section> value;

    public Sections(List<Section> value) {
        this.value = value;
    }

    /**
     * 새로운 구간을 등록하는 메서드
     *
     * @param newSection 추가하고자 하는 구간
     * @return 데이터가 변경된 Section의 List
     */
    public List<Section> add(Section newSection) {
        for (Section section : value) {
            validSection(newSection, section);

            if (newSection.canConnectWithUpStation(section)) {
                return connectWithUpStationId(newSection);
            }

            if (newSection.canConnectWithDownStation(section)) {
                return connectWithDownStationId(newSection);
            }
        }
        return List.of(newSection);
    }

    private void validSection(Section newSection, Section section) {
        if (newSection.isSameDownStationId(section) && newSection.isSameUpStationId(section)) {
            throw new IllegalArgumentException("해당 구간은 이미 등록되어 있습니다.");
        }
    }

    private List<Section> connectWithUpStationId(Section newSection) {
        Optional<Section> foundSection = findByUpStationId(newSection.getUpStationId());
        if (foundSection.isPresent()) {
            foundSection.get().updateUpStationId(newSection.getDownStationId());
            foundSection.get().reduceDistance(newSection.getDistance());
            return List.of(newSection, foundSection.get());
        }
        return List.of(newSection);
    }

    private Optional<Section> findByUpStationId(Long id) {
        return value.stream()
                .filter(section -> section.isSameUpStationId(id))
                .findFirst();
    }

    private List<Section> connectWithDownStationId(Section newSection) {
        Optional<Section> foundSection = findByDownStationId(newSection.getDownStationId());
        if (foundSection.isPresent()) {
            foundSection.get().updateDownStationId(newSection.getUpStationId());
            foundSection.get().reduceDistance(newSection.getDistance());
            return List.of(newSection, foundSection.get());
        }
        return List.of(newSection);
    }

    private Optional<Section> findByDownStationId(Long id) {
        return value.stream()
                .filter(section -> section.isSameDownStationId(id))
                .findFirst();
    }

    public Optional<Section> delete(Long sectionId) {
        Section section = findBySectionId(sectionId);
        value.remove(section);

        Optional<Section> updatedSection = findByUpStationId(section.getDownStationId());
        if (updatedSection.isPresent()) {
            updatedSection.get().updateUpStationId(section.getUpStationId());
            updatedSection.get().addDistance(section.getDistance());
        }
        return updatedSection;
    }

    private Section findBySectionId(Long id) {
        return value.stream()
                .filter(section -> section.isSameId(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 id인 구역을 찾을 수 없습니다."));
    }

    public List<Section> getValue() {
        return value;
    }
}
