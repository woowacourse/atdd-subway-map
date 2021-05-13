package wooteco.subway.service;

import java.util.List;
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

        priorSection.ifPresent(prior -> separatePriorSection(lineId, prior, sectionRequest));
        sectionDao.save(new Section(lineId, sectionRequest.toEntity()));
    }

    private void validateOnlyOneStationExists(Long lineId, SectionRequest sectionRequest) {
        Long sectionCountOfSameUp = sectionDao
                .countSection(lineId, sectionRequest.getUpStationId());
        Long sectionCountOfSameDown = sectionDao
                .countSection(lineId, sectionRequest.getDownStationId());

        if (sectionCountOfSameUp > 0 && sectionCountOfSameDown > 0) {
            throw new SubwayHttpException("추가하려는 구간의 상/하행역 둘다 노선에 존재");
        }
        if (sectionCountOfSameDown == 0 && sectionCountOfSameUp == 0) {
            throw new SubwayHttpException("추가하려는 구간의 상/하행역 모두 노선에 없음");
        }
    }

    private void separatePriorSection(Long lineId, Section priorSection,
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

    public void delete(Long lineId, Long stationId) {
        lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException("노선이 존재하지 않습니다"));

        validateLineHasMoreThanOneSection(lineId);

        List<Section> sections = sectionDao.countSectionByStationId(lineId, stationId);
        validateLineHasStation(sections);

        if (sections.size() == 2) {
            mergePriorSections(lineId, stationId, sections);
        } else if (sections.size() != 1) {
            throw new SubwayHttpException("삭제하려는 역을 포함하는 구간이 2개 이상임");
        }
        sectionDao.deleteSectionByStationId(lineId, stationId);
    }

    private void validateLineHasMoreThanOneSection(Long lineId) {
        List<Section> sections = sectionDao.listByLineId(lineId);
        if (sections.size() <= 1) {
            throw new SubwayHttpException("노선에 구간이 하나밖에 없어");
        }
    }

    private void validateLineHasStation(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new SubwayHttpException("노선에 존재하지 않는 역이야");
        }
    }

    private void mergePriorSections(Long lineId, Long stationId, List<Section> sections) {
        Section first = sections.get(0);
        Section second = sections.get(1);

        Section mergedSection = getMergedSection(lineId, stationId, first, second);
        sectionDao.save(mergedSection);
    }

    private Section getMergedSection(Long lineId, Long stationId, Section first, Section second) {
        Long newUpStationId;
        Long newDownStationId;
        if (correctSectionOrder(first, second, stationId)) {
            newUpStationId = second.getUpStationId();
            newDownStationId = first.getDownStationId();
        } else if (correctSectionOrder(second, first, stationId)) {
            newUpStationId = first.getUpStationId();
            newDownStationId = second.getDownStationId();
        } else {
            throw new IllegalArgumentException("구간에 삭제하려는 역이 없어");
        }

        int newDistance = first.getDistance() + second.getDistance();

        return new Section(
                lineId,
                newUpStationId,
                newDownStationId,
                newDistance);
    }

    private boolean correctSectionOrder(Section upper, Section lower, Long stationId) {
        return upper.getUpStationId().equals(stationId)
                && lower.getDownStationId().equals(stationId);
    }
}
