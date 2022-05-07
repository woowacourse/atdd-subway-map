package wooteco.subway.application;

import org.springframework.stereotype.Component;
import wooteco.subway.dto.DeleteSectionRequest;
import wooteco.subway.exception.NotDeletableSectionException;
import wooteco.subway.exception.NotFoundLineException;
import wooteco.subway.exception.NotFoundStationException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Component
public class DeleteSectionRequestValidator {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public DeleteSectionRequestValidator(LineRepository lineRepository,
                                         SectionRepository sectionRepository,
                                         StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public void validate(Long lineId, DeleteSectionRequest request) {
        if (isNotFoundLine(lineId)) {
            throw new NotFoundLineException(lineId);
        }
        if (isNotFoundStation(request.getStationId())) {
            throw new NotFoundStationException(request.getStationId());
        }
        if (isNotFoundStationOnLine(lineId, request.getStationId())) {
            throw new NotDeletableSectionException(request.getStationId());
        }
        if (hasOnlyOneSection(lineId)) {
            throw new NotDeletableSectionException(request.getStationId());
        }
    }

    private boolean isNotFoundLine(Long lineId) {
        return !lineRepository.existById(lineId);
    }

    private boolean isNotFoundStation(Long stationId) {
        return !stationRepository.existById(stationId);
    }

    private boolean isNotFoundStationOnLine(Long lineId, Long stationId) {
        return !sectionRepository.existByLineIdAndStationId(lineId, stationId);
    }

    private boolean hasOnlyOneSection(Long lineId) {
        return sectionRepository.findCountByLineId(lineId) == 1;
    }
}
