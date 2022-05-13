package wooteco.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wooteco.subway.dto.SectionRequest;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Long> findStationIdsInOrder() {
        List<Long> stationIds = new ArrayList<>();
        Long downStationId = 0L;
        for (Section section : sections) {
            if (isFinalUpSection(section)) {
                stationIds.add(section.getUpStationId());
                downStationId = section.getDownStationId();
                break;
            }
        }
        return findStationInOrder(downStationId, stationIds);
    }

    private boolean isFinalUpSection(Section section) {
        long upSectionCount = sections.stream()
                .filter(s -> section.getUpStationId().equals(s.getDownStationId()))
                .count();
        return upSectionCount == 0;
    }

    private List<Long> findStationInOrder(Long downStationId, List<Long> stationIds) {
        stationIds.add(downStationId);
        Optional<Section> optionalSection = sections.stream()
                .filter(section -> downStationId.equals(section.getUpStationId()))
                .findFirst();
        optionalSection.ifPresent(section -> findStationInOrder(section.getDownStationId(), stationIds));
        return stationIds;
    }

    public void validNonLinkSection(SectionRequest sectionRequest) {
        long linkedSectionCount = countLinkedSection(sectionRequest);
        if (linkedSectionCount == 0) {
            throw new IllegalArgumentException("연결할 section 이 존재하지 않습니다.");
        }
    }

    public long countLinkedSection(SectionRequest sectionRequest) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(sectionRequest.getDownStationId())
                        || section.getDownStationId().equals(sectionRequest.getUpStationId()))
                .count();
    }

    public void validSameStations(SectionRequest sectionRequest) {
        long linkedSectionCount = sections.stream()
                .filter(section -> section.getUpStationId().equals(sectionRequest.getUpStationId())
                        && section.getDownStationId().equals(sectionRequest.getDownStationId()))
                .count();
        if (linkedSectionCount > 0) {
            throw new IllegalArgumentException("상행역과 하행역이 노선에 이미 존재합니다.");
        }
    }

    public void validExistingSectionDistance(SectionRequest sectionRequest) {
        sections.stream()
                .filter(s -> s.getUpStationId().equals(sectionRequest.getUpStationId()))
                .findFirst()
                .ifPresent(section1 -> validSectionDistance(sectionRequest, section1));
    }

    private void validSectionDistance(SectionRequest sectionRequest, Section section) {
        if (section.getDistance() <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("추가될 구간의 길이가 기존 구간의 길이보다 깁니다.");
        }
    }

    public Optional<Section> findUpSection(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(upStationId))
                .findFirst();
    }

    public Optional<Section> findDownSection(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(downStationId))
                .findFirst();
    }

    public boolean requiredLink(Long stationId) {
        List<Section> sections = new ArrayList<>(this.sections);
        findUpSection(stationId).ifPresent(sections::remove);
        findDownSection(stationId).ifPresent(sections::remove);
        return sections.size() < findStationIdsInOrder().size() - 2;
    }

    public Section findLinkSection(Long lineId, Long stationId) {
        Section upSection = findUpSection(stationId).get();
        Section downSection = findDownSection(stationId).get();
        Long upStationId = upSection.getUpStationId();
        Long downStationId = downSection.getDownStationId();
        int distance = upSection.getDistance() + downSection.getDistance();

        return new Section(lineId, upStationId, downStationId, distance);
    }
}
