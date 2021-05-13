package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.line.LineDao;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.SectionDao;
import wooteco.subway.web.dto.SectionRequest;
import wooteco.subway.web.exception.NotFoundException;
import wooteco.subway.web.exception.SubwayHttpException;

@Service
@Transactional
public class SectionService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public void add(Long lineId, SectionRequest sectionRequest) {
        lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException("노선이 존재하지 않습니다"));
        validateOnlyOneStationExists(lineId, sectionRequest);

        Optional<Section> priorSection = sectionDao.priorSection(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId());

        priorSection.ifPresent(prior -> updatePriorSection(lineId, prior, sectionRequest));
        sectionDao.save(new Section(lineId, sectionRequest.toEntity()));
    }

    private void validateOnlyOneStationExists(Long lineId, SectionRequest sectionRequest) {
        Long sectionCountOfSameUp = sectionDao
                .countOfSection(lineId, sectionRequest.getUpStationId());
        Long sectionCountOfSameDown = sectionDao
                .countOfSection(lineId, sectionRequest.getDownStationId());

        if (sectionCountOfSameUp > 0 && sectionCountOfSameDown > 0) {
            throw new SubwayHttpException("");
        }

        if (sectionCountOfSameDown == 0 && sectionCountOfSameUp == 0) {
            throw new SubwayHttpException("");
        }
    }

    private void updatePriorSection(Long lineId, Section priorSection,
            SectionRequest sectionRequest) {
        validateDistance(priorSection, sectionRequest);
        sectionDao.delete(priorSection.getId());
        addUpdatedPrior(lineId, priorSection, sectionRequest);
    }

    private void validateDistance(Section priorSection, SectionRequest sectionRequest) {
        if (priorSection.getDistance() <= sectionRequest.getDistance()) {
            throw new SubwayHttpException("추가하려는 구간의 거리가 기존 구간거리보다 크거나 같음");
        }
    }

    private void addUpdatedPrior(Long lineId, Section priorSection, SectionRequest sectionRequest) {
        Long priorUpId = priorSection.getUpStationId();
        Long priorDownId = priorSection.getDownStationId();
        Long requestUpId = sectionRequest.getUpStationId();
        Long requestDownId = sectionRequest.getDownStationId();

        int updatedDistance = priorSection.getDistance() - sectionRequest.getDistance();

        if (priorUpId.equals(requestUpId)) {
            sectionDao.save(new Section(lineId, requestDownId, priorDownId, updatedDistance));
        } else if (priorDownId.equals(requestDownId)) {
            sectionDao.save(new Section(lineId, priorUpId, requestUpId, updatedDistance));
        }
    }

}
