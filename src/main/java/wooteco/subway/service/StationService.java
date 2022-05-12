package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.StationResponse;
import wooteco.subway.ui.dto.StationRequest;

@Service
@Transactional
public class StationService {

    private static final String DUPLICATED_NAME_ERROR_MESSAGE = "중복된 이름이 존재합니다.";

    private final SectionService sectionService;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public StationService(SectionService sectionService, StationDao stationDao, LineDao lineDao) {
        this.sectionService = sectionService;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public StationResponse save(StationRequest stationRequest) {
        validDuplicatedName(stationRequest.getName());
        Station station = new Station(stationRequest.getName());
        Long id = stationDao.save(station);
        return new StationResponse(id, stationRequest.getName());
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

    private void deleteStationInSections(Long id) {
        List<Long> lineIds = lineDao.findAll()
                .stream()
                .map(Line::getId)
                .collect(Collectors.toList());

        for(Long lineId : lineIds) {
            sectionService.deleteById(lineId, id);
        }
    }
}
