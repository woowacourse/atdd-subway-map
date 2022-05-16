package wooteco.subway.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.ui.dto.StationRequest;

@Service
@Transactional
public class StationService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";

    private final SectionService sectionService;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(SectionService sectionService, StationDao stationDao, SectionDao sectionDao) {
        this.sectionService = sectionService;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        String name = stationRequest.getName();
        validDuplicatedName(name);

        Long id = stationDao.save(stationRequest.toEntity());
        return new StationResponse(id, name);
    }

    private void validDuplicatedName(String name) {
        if (stationDao.existsByName(name)) {
            throw new IllegalArgumentException(DUPLICATED_NAME_ERROR_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        deleteStationInSections(id);
        stationDao.deleteById(id);
    }

    private void deleteStationInSections(Long stationId) {
        List<Section> sections = sectionDao.findByStationId(stationId);
        Map<Long, List<Section>> sectionsMap = initSectionsMap(sections);

        for (Entry<Long, List<Section>> sectionsInfo : sectionsMap.entrySet()) {
            Sections lineIdSections = new Sections(sectionsInfo.getValue());
            sectionService.delete(lineIdSections, stationId);
        }
    }

    private Map<Long, List<Section>> initSectionsMap(List<Section> sections) {
        Map<Long, List<Section>> map = new HashMap<>();
        for (Section section : sections) {
            Long lineId = section.getLineId();
            List<Section> sectionList = map.getOrDefault(lineId, new LinkedList<>());
            sectionList.add(section);
            map.put(lineId, sectionList);
        }
        return map;
    }
}
