package wooteco.subway.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
        validateLineId(lineId);

        validateOnlyOneStationExists(lineId, sectionRequest);

        shortenPriorSectionIfExists(lineId, sectionRequest);
        sectionDao.save(new Section(lineId, sectionRequest.toEntity()));
    }

    private void shortenPriorSectionIfExists(Long lineId, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();

        sectionDao.priorSection(lineId, upStationId, downStationId)
                .ifPresent(prior -> shortenPriorSection(lineId, prior, sectionRequest));
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

    private void shortenPriorSection(Long lineId, Section priorSection,
            SectionRequest sectionRequest) {
        validateDistance(priorSection, sectionRequest);
        sectionDao.delete(priorSection.getId());
        addUpdatedPrior(lineId, priorSection, sectionRequest);
    }

    private void validateDistance(Section priorSection, SectionRequest sectionRequest) {
        Integer priorDistance = priorSection.getDistance();
        Integer newDistance = sectionRequest.getDistance();
        if (priorDistance <= newDistance) {
            throw new SubwayHttpException("추가하려는 구간의 거리가 기존 구간거리보다 크거나 같음");
        }
    }

    private void addUpdatedPrior(Long lineId, Section priorSection, SectionRequest sectionRequest) {
        Long priorUpId = priorSection.getUpStationId();
        Long priorDownId = priorSection.getDownStationId();
        Long requestUpId = sectionRequest.getUpStationId();
        Long requestDownId = sectionRequest.getDownStationId();
        Integer newDistance = priorSection.getDistance() - sectionRequest.getDistance();

        validateStationExistsInSections(priorUpId, priorDownId, requestUpId, requestDownId);

        Long newUpId = null;
        Long newDownId = null;

        if (priorUpId.equals(requestUpId)) {
            newUpId = requestDownId;
            newDownId = priorDownId;
        } else if (priorDownId.equals(requestDownId)) {
            newUpId = priorUpId;
            newDownId = requestUpId;
        }
        Section updatedPriorSection = new Section(lineId, newUpId, newDownId, newDistance);
        sectionDao.save(updatedPriorSection);
    }

    // 굳이 필요한가?
    private void validateStationExistsInSections(Long priorUpId, Long priorDownId,
            Long requestUpId, Long requestDownId) {
        if (!priorUpId.equals(requestUpId) && !priorDownId.equals(requestDownId)) {
            throw new IllegalArgumentException("삭제하려는 역이 기존구간에 존재하지 않음");
        }
    }

    public void delete(Long lineId, Long stationId) {
        validateLineId(lineId);
        validateLineHasMoreThanOneSection(lineId);

        List<Section> sections = sectionDao.countSectionByStationId(lineId, stationId);

        validateLineHasStation(sections);
        validateSize(sections.size());

        if (sections.size() == 2) {
            mergePriorSections(lineId, stationId, sections);
        }
        sectionDao.deleteSectionByStationId(lineId, stationId);
    }

    private void validateSize(int size) {
        if (!Arrays.asList(1, 2).contains(size)) {
            throw new SubwayHttpException("삭제하려는 역을 포함하는 구간이 2개 이상임");
        }
    }

    private void validateLineId(Long lineId) {
        try {
            lineDao.findById(lineId);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("노선이 존재하지 않습니다");
        }
    }

    private void validateLineHasMoreThanOneSection(Long lineId) {
        List<Section> sections = sectionDao.findSectionsByLineId(lineId);
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
        validate(first, second, stationId);

        Integer newDistance = first.getDistance() + second.getDistance();
        Long newUpStationId = null;
        Long newDownStationId = null;

        if (isCorrectSectionOrder(first, second, stationId)) {
            newUpStationId = second.getUpStationId();
            newDownStationId = first.getDownStationId();
        } else if (isCorrectSectionOrder(second, first, stationId)) {
            newUpStationId = first.getUpStationId();
            newDownStationId = second.getDownStationId();
        }

        return new Section(
                lineId,
                newUpStationId,
                newDownStationId,
                newDistance);
    }

    // 굳이 필요한가?
    private void validate(Section first, Section second, Long stationId) {
        Long firstDownId = first.getDownStationId();
        Long firstUpId = first.getUpStationId();
        Long secondDownId = second.getDownStationId();
        Long secondUpId = second.getUpStationId();

        List<Long> ids = Arrays.asList(firstUpId, firstDownId, secondUpId, secondDownId);

        if (!ids.contains(stationId)) {
            throw new IllegalArgumentException("구간에 삭제하려는 역이 없어");
        }
    }

    private boolean isCorrectSectionOrder(Section upper, Section lower, Long stationId) {
        return upper.getUpStationId().equals(stationId)
                && lower.getDownStationId().equals(stationId);
    }
}
