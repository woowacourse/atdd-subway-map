package wooteco.subway.station.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.dao.SectionDao;
import wooteco.subway.line.domain.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationDto;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public StationDto save(StationDto stationRequest) {
        validateDuplicate(stationRequest.getName());
        return StationDto.toDto(stationDao.save(StationDto.toStation(stationRequest)));
    }

    private void validateDuplicate(final String name) {
        if (stationDao.findByName(name).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 역입니다.");
        }
    }

    public List<StationDto> findAll() {
        return StationDto.toDtos(stationDao.findAll());
    }

    @Transactional
    public void delete(Long id) {
        List<Section> sections = sectionDao.findByStationId(id);
        if (!sections.isEmpty()) {
            throw new IllegalStateException("[ERROR] 구간에 역이 등록되어 삭제할 수 없습니다.");
        }
        stationDao.delete(id);
    }
}
