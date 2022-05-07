package wooteco.subway.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.DuplicateSectionException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.exception.NotSplittableSectionException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository,
                          StationRepository stationRepository,
                          SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public Section addSection(Long lineId, SectionRequest request) {
        Long upStationId = request.getUpStationId();
        Long downStationId = request.getDownStationId();
        int distance = request.getDistance();

        validateIds(lineId, upStationId, downStationId);

        Section newSection = new Section(lineId, upStationId, downStationId, distance);
        findExistSection(lineId, upStationId, downStationId)
            .ifPresent(existSection -> updateExistSection(newSection, existSection));

        return sectionRepository.save(newSection);
    }

    private void validateIds(Long lineId, Long upStationId, Long downStationId) {
        if (isSameStationId(upStationId, downStationId)) {
            throw new NotSplittableSectionException(upStationId, downStationId);
        }
        if (isNotFoundLine(lineId)) {
            throw new NotFoundLineException(lineId);
        }
        if (isNotFoundStation(upStationId)) {
            throw new NotFoundStationException(upStationId);
        }
        if (isNotFoundStation(downStationId)) {
            throw new NotFoundStationException(downStationId);
        }
        if (isDuplicateSection(lineId, upStationId, downStationId)) {
            throw new DuplicateSectionException(lineId, upStationId, downStationId);
        }
        if (isNotFoundStationsOnLine(lineId, upStationId, downStationId)) {
            throw new NotSplittableSectionException(upStationId, downStationId);
        }
    }

    private boolean isSameStationId(Long upStationId, Long downStationId) {
        return upStationId.equals(downStationId);
    }

    private boolean isNotFoundLine(Long lineId) {
        return !lineRepository.existById(lineId);
    }

    private boolean isNotFoundStation(Long upStationId) {
        return !stationRepository.existById(upStationId);
    }

    private boolean isDuplicateSection(Long lineId, Long upStationId, Long downStationId) {
        return sectionRepository.existByLineIdAndStationId(lineId, upStationId)
            && sectionRepository.existByLineIdAndStationId(lineId, downStationId);
    }

    private boolean isNotFoundStationsOnLine(Long lineId, Long upStationId, Long downStationId) {
        return !sectionRepository.existByLineIdAndStationId(lineId, upStationId) &&
            !sectionRepository.existByLineIdAndStationId(lineId, downStationId);
    }

    private Optional<Section> findExistSection(Long lineId, Long upStationId, Long downStationId) {
        return findSameUpStationExistSection(lineId, upStationId)
            .or(() -> findSameDownStationExistSection(lineId, downStationId));
    }

    private Optional<Section> findSameUpStationExistSection(Long lineId, Long upStationId) {
        return sectionRepository.findByLineIdAndUpStationId(lineId, upStationId);
    }

    private Optional<Section> findSameDownStationExistSection(Long lineId, Long downStationId) {
        return sectionRepository.findByLineIdAndDownStationId(lineId, downStationId);
    }

    private void updateExistSection(Section newSection, Section existSection) {
        Section sectionForUpdate = existSection.split(newSection);
        sectionRepository.update(sectionForUpdate);
    }
}
