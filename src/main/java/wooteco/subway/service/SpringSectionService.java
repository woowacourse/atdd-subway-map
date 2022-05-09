package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.infra.dao.SectionDao;
import wooteco.subway.infra.entity.SectionEntity;
import wooteco.subway.service.dto.SectionServiceRequest;

@Service
public class SpringSectionService implements SectionService {

    private final SectionDao sectionRepository;

    public SpringSectionService(SectionDao sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public void save(Long lineId, SectionServiceRequest sectionServiceRequest) {
        sectionRepository.save(new SectionEntity(lineId, sectionServiceRequest.getUpStationId(),
                sectionServiceRequest.getDownStationId(), sectionServiceRequest.getDistance()));
    }

    @Override
    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        sectionRepository.deleteByLineIdAndStationId(lineId, stationId);
    }
}
