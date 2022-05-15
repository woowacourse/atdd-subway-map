package wooteco.subway.application;

import org.springframework.stereotype.Component;
import wooteco.subway.application.exception.NotFoundLineException;
import wooteco.subway.application.exception.NotFoundStationException;
import wooteco.subway.application.exception.UndeletableSectionException;
import wooteco.subway.dto.DeleteSectionRequest;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;

@Component
public class DeleteSectionRequestValidator {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public DeleteSectionRequestValidator(LineRepository lineRepository,
                                         StationRepository stationRepository,
                                         SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void validate(Long lineId, DeleteSectionRequest request) {
        if (isNotFoundLine(lineId)) {
            throw new NotFoundLineException(lineId);
        }
        if (isNotFoundStation(request.getStationId())) {
            throw new NotFoundStationException(request.getStationId());
        }
        if (isNotFoundStationOnLine(lineId, request.getStationId()) || hasOnlyOneSection(lineId)) {
            throw new UndeletableSectionException(lineId, request.getStationId());
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
