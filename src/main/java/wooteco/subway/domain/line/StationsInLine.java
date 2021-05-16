package wooteco.subway.domain.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.illegal.BothStationInLineException;
import wooteco.subway.exception.illegal.IllegalInputException;
import wooteco.subway.exception.nosuch.BothStationNotInLineException;

public class StationsInLine {
    private final List<Section> sections;
    private final List<Long> stationIds;

    public StationsInLine() {
        sections = new ArrayList<>();
        stationIds = new ArrayList<>();
    }

    public StationsInLine(List<Station> stations) {
        this.sections = new ArrayList<>();
        this.stationIds = stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());
    }

    public StationsInLine(List<Section> sections1, List<Long> orderedStationId) {
        this.sections = sections1;
        this.stationIds = orderedStationId;
    }

    //
    // public StationsInLine(List<Section> sections) {
    //     this.sections = sections;
    //     // this.stationIds = stations.stream()
    //     //     .map(Station::getId)
    //     //     .collect(Collectors.toList());
    // }

    public static StationsInLine of(List<Section> sections) {
        //내가 필요한것: List<Station> ->
        //여기서 주어진것: 모든 sections

        //이제 얻어낼 것: 정렬된 List<Section>을 가진다면?

        List<Long> upStationIds = sections.stream()
            .map(Section::getUpStationId)
            .collect(Collectors.toList());

        List<Long> downStationIds = sections.stream()
            .map(Section::getDownStationId)
            .collect(Collectors.toList());

        Long startStationId = upStationIds.stream()
            .filter(id -> !downStationIds.contains(id))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);

        List<Long> orderedStationId = new ArrayList<>();
        List<Section> sections1 = new ArrayList<>();

        Long finalStartStationId1 = startStationId;
        Long upStationId = sections.stream()
            .filter(section -> section.getUpStationId() == finalStartStationId1)
            .peek(sections1::add)
            .map(Section::getUpStationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
        orderedStationId.add(upStationId);


        while(orderedStationId.size() != sections.size()+1) {
            long finalStartStationId = startStationId;

            Long downStationId = sections.stream()
                .filter(section -> section.getUpStationId() == finalStartStationId)
                .peek(sections1::add)
                .map(Section::getDownStationId)
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
            orderedStationId.add(downStationId);

            startStationId = downStationId;
        }

        return new StationsInLine(sections1, orderedStationId);
    }


    public static StationsInLine from(Map<Station, Station> sections) {
        List<Station> stations = new ArrayList<>();
        Station startStation = sections.keySet().stream()
            .filter(station -> !sections.containsValue(station))
            .findAny()
            .orElseThrow(IllegalInputException::new);
        stations.add(startStation);

        for (int i = 0; i < sections.size(); i++) {
            Station endStation = sections.get(startStation);
            stations.add(endStation);
            startStation = endStation;
        }

        return new StationsInLine(stations);
    }

    public void validStations(long upStationId, long downStationId) {
        if (stationIds.containsAll(Arrays.asList(upStationId, downStationId))) {
            throw new BothStationInLineException();
        }

        System.out.println("~~!@~!@~!@"+ stationIds);
        if (!stationIds.contains(upStationId) && !stationIds.contains(downStationId)) {
            throw new BothStationNotInLineException();
        }
    }

    public boolean isEndStations(long upStationId, long downStationId) {
        return stationIds.get(0) == downStationId || stationIds.get(stationIds.size() - 1) == upStationId;
    }

    public boolean contains(long stationId) {
        return stationIds.contains(stationId);
    }

    public boolean canNotDelete() {
        return sections.size() == 1;
    }

    // public List<Station> getStations() {
        // List<Station> s = new ArrayList<>();
        // s.add(sections.get(0).getUpStationId());
        // for (Section section : sections) {
        //     s.add(section.getDownStationId());
        // }
        // return s;
    // }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public Section getUpSection(long stationId) {
        return sections.stream()
            .filter(section -> section.getDownStationId() == stationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public Section getDownSection(long stationId) {
        return sections.stream()
            .filter(section -> section.getUpStationId() == stationId)
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }

    public List<Section> getSectionsWithStation(long stationId) {
        return sections.stream()
            .filter(section -> section.contains(stationId))
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StationsInLine that = (StationsInLine)o;
        return Objects.equals(sections, that.sections) && Objects.equals(stationIds, that.stationIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections, stationIds);
    }
}
