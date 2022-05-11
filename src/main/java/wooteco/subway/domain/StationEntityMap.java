package wooteco.subway.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import wooteco.subway.entity.SectionViewEntity;
import wooteco.subway.entity.StationEntity;

public class StationEntityMap {

    private final Map<Long, StationEntity> value;

    public StationEntityMap(Map<Long, StationEntity> value) {
        this.value = value;
    }

    public static StationEntityMap of(List<SectionViewEntity> sectionViewEntities) {
        Map<Long, StationEntity> stationEntityMap = toUniqueStationList(sectionViewEntities)
                .stream()
                .collect(Collectors.toMap(StationEntity::getId, (entity) -> entity));

        return new StationEntityMap(stationEntityMap);
    }

    private static Set<StationEntity> toUniqueStationList(List<SectionViewEntity> sectionViewEntities) {
        Set<StationEntity> stations = new HashSet<>();
        stations.addAll(extractUpStations(sectionViewEntities));
        stations.addAll(extractDownStations(sectionViewEntities));
        return stations;
    }

    private static List<StationEntity> extractUpStations(List<SectionViewEntity> sectionViewEntities) {
        return sectionViewEntities.stream()
                .map(SectionViewEntity::getUpStation)
                .collect(Collectors.toList());
    }

    private static List<StationEntity> extractDownStations(List<SectionViewEntity> sectionViewEntities) {
        return sectionViewEntities.stream()
                .map(SectionViewEntity::getDownStation)
                .collect(Collectors.toList());
    }

   public int getSize() {
        return value.size();
   }

   public StationEntity findEntityOfId(Long stationId){
        return value.get(stationId);
   }
}
