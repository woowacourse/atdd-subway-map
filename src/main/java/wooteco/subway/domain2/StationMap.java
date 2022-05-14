package wooteco.subway.domain2;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionEntity2;
import wooteco.subway.entity.StationEntity;

public class StationMap {

    private final Map<Long, Station> value;

    private StationMap(Map<Long, Station> value) {
        this.value = value;
    }

    public static StationMap of(List<SectionEntity2> sectionEntities) {
        Map<Long, Station> stationEntityMap = toUniqueStationList(sectionEntities)
                .stream()
                .collect(Collectors.toMap(StationEntity::getId, StationEntity::toDomain));

        return new StationMap(stationEntityMap);
    }

    private static Set<StationEntity> toUniqueStationList(List<SectionEntity2> sectionEntities) {
        Set<StationEntity> stations = new HashSet<>();
        stations.addAll(extractUpStations(sectionEntities));
        stations.addAll(extractDownStations(sectionEntities));
        return stations;
    }

    private static List<StationEntity> extractUpStations(List<SectionEntity2> sectionEntities) {
        return sectionEntities.stream()
                .map(SectionEntity2::getUpStation)
                .collect(Collectors.toList());
    }

    private static List<StationEntity> extractDownStations(List<SectionEntity2> sectionEntities) {
        return sectionEntities.stream()
                .map(SectionEntity2::getDownStation)
                .collect(Collectors.toList());
    }

   public int getSize() {
        return value.size();
   }

   public Station findEntityOfId(Long stationId){
        return value.get(stationId);
   }
}
