package wooteco.subway.domain;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {
    @NotBlank(message = "구간은 하나이상 등록되어 있어야 합니다.")
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean isLastStation(long stationId) {
        return sections.stream()
                .noneMatch(section -> section.getUpStationId() == stationId);
    }

    public List<Long> sortStations() {
        List<Long> stations = new ArrayList<>();
        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStationId());

        long station = firstSection.getDownStationId();
        while (!isLastStation(station)) {
            stations.add(station);
            station = findNextSection(station).getDownStationId();
        }

        stations.add(findLastSection().getDownStationId());
        return stations.stream()
                .collect(Collectors.toList());
    }

    private Section findLastSection() {
        return sections.stream()
                .filter(section -> isLastStation(section.getDownStationId()))
                .findFirst()
                .orElse(sections.get(0));
    }

    private Section findNextSection(long stationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId() == stationId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("다음 구간이 존재하지 않습니다."));
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(section -> isFirstStation(section.getUpStationId()))
                .findFirst()
                .orElse(sections.get(0));
    }

    public boolean isFirstStation(long stationId) {
        return sections.stream()
                .noneMatch(section -> section.getDownStationId() == stationId);
    }

    public boolean isLessThanOneSection() {
        return sections.size() == 1;
    }
}
