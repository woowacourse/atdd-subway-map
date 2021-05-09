package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.repository.SectionDao;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(SectionRequest sectionRequest) {
        SectionEntity sectionEntity = new SectionEntity(sectionRequest.getLineId(),
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance());

        SectionEntity newSectionEntity = sectionDao.save(sectionEntity);

        return new SectionResponse(newSectionEntity);
    }
}
