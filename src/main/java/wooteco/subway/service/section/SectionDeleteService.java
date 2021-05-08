package wooteco.subway.service.section;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.exception.HttpException;

@Transactional
@Service
public class SectionDeleteService {
    private static final String SECTION_DELETE_ERROR_MESSAGE = "구간 삭제 에러";
    private final SectionDao sectionDao;

    public SectionDeleteService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void deleteSectionById(Long lineId, Long stationIdToDelete) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        List<Section> sectionsWithStationToDelete = sections.stream()
            .filter(section ->
                section.getUpStationId().equals(stationIdToDelete)
                    || section.getDownStationId().equals(stationIdToDelete))
            .collect(Collectors.toList());
        if (sectionsWithStationToDelete.size() == 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "노선에 삭제할 역이 존재하지 않습니다.");
        }
        if (sections.size() == 1) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "한 개의 구간만 존재하는 노선은 구간을 삭제할 수 없습니다.");
        }
        if (sectionsWithStationToDelete.size() == 1) {
            Section sectionToDelete = sectionsWithStationToDelete.get(0);
            sectionDao.deleteById(sectionToDelete.getId());
        }
        if (sectionsWithStationToDelete.size() == 2) {
            Section newUpStationSection = sectionsWithStationToDelete.stream()
                .filter(section -> section.getDownStationId().equals(stationIdToDelete))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(SECTION_DELETE_ERROR_MESSAGE));
            Section newDownStationSection = sectionsWithStationToDelete.stream()
                .filter(section -> section.getUpStationId().equals(stationIdToDelete))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(SECTION_DELETE_ERROR_MESSAGE));

            int newDistance = newUpStationSection.getDistance() + newDownStationSection.getDistance();
            Section newSection
                = new Section(lineId, newUpStationSection.getUpStationId(), newDownStationSection.getDownStationId(), newDistance);
            sectionDao.deleteById(newUpStationSection.getId());
            sectionDao.deleteById(newDownStationSection.getId());
            sectionDao.save(newSection);
        }
    }
}
