package wooteco.subway.application;

import org.springframework.stereotype.Component;
import wooteco.subway.application.exception.DuplicateSectionException;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.application.exception.UnaddableSectionException;
import wooteco.subway.dto.AddSectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Component
public class AddSectionRequestValidator {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public AddSectionRequestValidator(LineRepository lineRepository,
                                      StationRepository stationRepository,
                                      SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void validate(Long lineId, AddSectionRequest request) {
        Long upStationId = request.getUpStationId();
        Long downStationId = request.getDownStationId();

        if (isSameStationId(upStationId, downStationId)) {
            throw new UnaddableSectionException(lineId, upStationId, downStationId);
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
            throw new UnaddableSectionException(lineId, upStationId, downStationId);
        }
    }

    private boolean isSameStationId(Long upStationId, Long downStationId) {
        return upStationId.equals(downStationId);
    }

    private boolean isNotFoundLine(Long lineId) {
        return !lineRepository.existById(lineId);
    }

    private boolean isNotFoundStation(Long stationId) {
        return !stationRepository.existById(stationId);
    }

    private boolean isDuplicateSection(Long lineId, Long upStationId, Long downStationId) {
        return sectionRepository.existByLineIdAndStationId(lineId, upStationId)
            && sectionRepository.existByLineIdAndStationId(lineId, downStationId);
    }

    private boolean isNotFoundStationsOnLine(Long lineId, Long upStationId, Long downStationId) {
        return isNotFoundStationOnLine(lineId, upStationId) &&
            isNotFoundStationOnLine(lineId, downStationId);
    }

    private boolean isNotFoundStationOnLine(Long lineId, Long stationId) {
        return !sectionRepository.existByLineIdAndStationId(lineId, stationId);
    }
}
