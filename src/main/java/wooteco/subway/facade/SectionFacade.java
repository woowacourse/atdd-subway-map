package wooteco.subway.facade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.web.dto.SectionRequest;

@Service
@Transactional
public class SectionFacade {

    private final LineService lineService;
    private final SectionService sectionService;

    public SectionFacade(LineService lineService, SectionService sectionService) {
        this.lineService = lineService;
        this.sectionService = sectionService;
    }

    public void add(Long lineId, SectionRequest sectionRequest) {
        lineService.findLine(lineId);
        sectionService.validateOnlyOneStationExists(lineId, sectionRequest);

        sectionService.shortenPriorSectionIfExists(lineId, sectionRequest);
        sectionService.save(lineId, sectionRequest.toEntity());
    }

    public void delete(Long lineId, Long stationId) {
        lineService.findLine(lineId);
        sectionService.validateLineHasMoreThanOneSection(lineId);
        sectionService.mergePriorSectionsIfExists(lineId, stationId);
        sectionService.deleteSectionByStationId(lineId, stationId);
    }
}
