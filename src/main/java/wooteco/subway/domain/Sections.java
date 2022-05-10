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
            if (countFinalUpStation(section) == 0) {
                stationIds.add(section.getUpStationId());
                downStationId = section.getDownStationId();
                break;
            }
        }
        return findStationInOrder(downStationId, stationIds);
    }

    private long countFinalUpStation(Section section) {
        return sections.stream()
                .filter(s -> section.getUpStationId().equals(s.getDownStationId()))
                .count();
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
        long linkedSectionCount = sections.stream()
                .filter(section -> section.getUpStationId().equals(sectionRequest.getDownStationId())
                        || section.getDownStationId().equals(sectionRequest.getUpStationId()))
                .count();
        if (linkedSectionCount == 0) {
            throw new IllegalArgumentException("연결할 section 이 존재하지 않습니다.");
        }
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
                .ifPresent(section1 -> {
                    validSectionDistance(sectionRequest, section1);
                });
    }

    private void validSectionDistance(SectionRequest sectionRequest, Section section) {
        if (section.getDistance() <= sectionRequest.getDistance()) {
            throw new IllegalArgumentException("추가될 구간의 길이가 기존 구간의 길이보다 깁니다.");
        }
    }
}
