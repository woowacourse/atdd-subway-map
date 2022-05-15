package wooteco.subway.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NotExistException;

@Service
@Transactional
public class StationService {

    private static final int DELETE_FAIL = 0;

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public StationResponse save(StationRequest request) {
        final Station station = new Station(request.getName());

        final Long savedId = stationDao.save(station);

        return new StationResponse(savedId, station.getName());
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAll() {
        return stationDao.findAll()
                .stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(toUnmodifiableList());
    }

    public void deleteById(Long id) {
        Set<Long> stationIds = new HashSet<>();
        final List<Section> sections = sectionDao.findAll();

        getStationIds(stationIds, sections);
        validateDeletable(id, stationIds);

        final int isDeleted = stationDao.deleteById(id);
        if (isDeleted == DELETE_FAIL) {
            throw new NotExistException("존재하지 않는 지하철 역입니다.");
        }
    }

    private void getStationIds(Set<Long> stationIds, List<Section> sections) {
        for (Section section : sections) {
            final Long upStationId = section.getUpStationId();
            final Long downStationId = section.getDownStationId();

            stationIds.add(upStationId);
            stationIds.add(downStationId);
        }
    }

    private void validateDeletable(Long id, Set<Long> stationIds) {
        if (stationIds.contains(id)) {
            throw new IllegalArgumentException("구간을 먼저 삭제해주세요.");
        }
    }
}
