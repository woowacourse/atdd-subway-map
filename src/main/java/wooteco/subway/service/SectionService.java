package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.repository.SectionRepository;
import wooteco.subway.domain.repository.StationRepository;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.utils.exception.CannotCreateClassException;
import wooteco.subway.utils.exception.NotFoundException;

@Transactional
@Service
public class SectionService {
    private static final String NOT_FOUND_STATION_MESSAGE = "[ERROR] %d 식별자에 해당하는 역을 찾을수 없습니다.";

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public Section create(Long lineId, SectionRequest sectionRequest) {

        Sections sections = new Sections(sectionRepository.findAllByLineId(lineId));
        sections.checkDuplicateSection(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, sectionRequest.getUpStationId())));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_STATION_MESSAGE, sectionRequest.getDownStationId())));

        Section section = Section.create(lineId, upStation, downStation, sectionRequest.getDistance());
        Section findSection = sectionRepository.findByUpStationIdWithLineId(sectionRequest.getUpStationId(), lineId)
                .orElseThrow(() -> new NotFoundException("[ERROR] 상행 역을 찾을 수 없습니다."));
        validateDistance(sectionRequest, findSection);
        //모든 station중 contain했을 때, 둘다 아니라면 예외

        //TODO: request의 upStation이 기존의 upStation과 같을때, 기존보다 distance가 작다면
        // new Section(새로운 downStation, 기존 downStation, 기존 섹션 길이 - 새로운 색션 길이)
        // delete 기존 섹션 -> 새로운 섹션 save

        //(1,3)(3,2)(2,5): (2,4)
        return sectionRepository.save(section);
    }

    private void validateDistance(SectionRequest sectionRequest, Section findSection) {
        if (findSection.isEqualsAndSmallerThan(sectionRequest.getDistance())) {
            throw new CannotCreateClassException("[ERROR] 구간의 길이가 기존보다 크거나 같습니다.");
        }
    }

}
