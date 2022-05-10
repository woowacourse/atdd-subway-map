package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.SectionEntity;
import wooteco.subway.domain.StationEntity;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, StationService stationService) {
        this.sectionDao = sectionDao;
        this.stationService = stationService;
    }

    public void createSection(Long lineId, Long upDestinationId, Long downDestinationId, int distance) {
        sectionDao.save(lineId, upDestinationId, downDestinationId, distance);
    }

    public List<StationEntity> findStationsByLineId(Long lineId) {
        List<SectionEntity> sections = sectionDao.findByLineId(lineId);
        Long upDestinationId = findId(sections);
        Map<Long, Long> stationIds = sections.stream()
            .collect(Collectors.toMap(SectionEntity::getUpStationId,
                SectionEntity::getDownStationId, (a, b) -> b));

        List<StationEntity> sectionEntities = new ArrayList<>();
        sectionEntities.add(stationService.findById(upDestinationId));

        Long key = upDestinationId;
        for (int i = 0; i < stationIds.size(); i++) {
            key = stationIds.get(key);
            sectionEntities.add(stationService.findById(key));
        }

        return sectionEntities;
    }

    public Long findId(List<SectionEntity> sections) {
        Map<Long, Integer> counts = sections.stream()
            .map(SectionEntity::getUpStationId)
            .collect(Collectors.toMap(id -> id, id -> 1, Integer::sum));

        for (Long id : counts.keySet()) {
            if (counts.get(id).equals(1)) {
                return id;
            }
        }

        throw new IllegalArgumentException("");
    }
}
