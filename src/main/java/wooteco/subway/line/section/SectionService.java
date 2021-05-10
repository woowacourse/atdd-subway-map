package wooteco.subway.line.section;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(final long lineId, final SectionRequest sectionRequest) {
        final List<Section> list = sectionDao.findByLineId(lineId);
        if (list.isEmpty()) {
            final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
            return SectionResponse.from(section);
        }
        final Sections sections = findSectionsByLineId(lineId);
        final Long upStationId = sectionRequest.getUpStationId();
        final Long downStationId = sectionRequest.getDownStationId();

        sections.validateBothExistentStation(upStationId, downStationId);
        sections.validateNoneExistentStation(upStationId, downStationId);

        final long existentStationId = sections.matchedStationId(upStationId, downStationId);
        if (sections.isAddableEndStation(existentStationId, upStationId, downStationId)) {
            return SectionResponse.from(sectionDao.save(sectionRequest.toEntity(lineId)));
        }



        // 존재하는 역이 입력된 값에서 상행이다.
        if (existentStationId == upStationId) {
            final Section existentSection = sections.findExistentUpStation(existentStationId);
            if (existentSection.getDistance() <= sectionRequest.getDistance()) {
                throw new RuntimeException("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
            }
            // 존재하는 역이 상행인 구간을 찾는다.
            final Section updatedSection = new Section(existentSection.getId(), existentSection.getLineId(),
                existentSection.getUpStationId(), downStationId, sectionRequest.getDistance());

            // 존재하는 구간의 하행을 새로 추가할 역으로 바꾼다.
            sectionDao.update(updatedSection);

            // 추가할 역과 존재하는 역의 하행을 이은 구간을 저장한다.
            final Section section = sectionDao.save(new Section(lineId, downStationId,
                existentSection.getDownStationId(), existentSection.getDistance() - sectionRequest.getDistance()));
            return SectionResponse.from(section);
        }

        // 존재하는 역이 입력된 값에서 하행이다.
        if (existentStationId == downStationId) {
            final Section existentSection = sections.findExistentDownStation(existentStationId);
            if (existentSection.getDistance() <= sectionRequest.getDistance()) {
                throw new RuntimeException("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
            }
            // 존재하는 역이 하행인 구간을 찾는다.
            final Section updatedSection = new Section(existentSection.getId(), existentSection.getLineId(),
                upStationId, existentSection.getDownStationId(), sectionRequest.getDistance());

            // 존재하는 구간의 상행을 새로 추가할 역으로 바꾼다.
            sectionDao.update(updatedSection);

            // 존재하는 역의 상행과 추가할 역을 이은 구간을 저장한다.
            final Section section = sectionDao.save(new Section(lineId, existentSection.getUpStationId(),
                upStationId, existentSection.getDistance() - sectionRequest.getDistance()));
            return SectionResponse.from(section);
        }

        final Section section = sectionDao.save(sectionRequest.toEntity(lineId));
        return SectionResponse.from(section);
    }

    public Sections findSectionsByLineId(final Long lineId) {
        return new Sections(sectionDao.findByLineId(lineId));
    }
}
