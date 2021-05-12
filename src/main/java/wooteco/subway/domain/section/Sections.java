package wooteco.subway.domain.section;

import org.springframework.http.HttpStatus;
import wooteco.subway.exception.SubwayException;

import java.util.*;

public class Sections {
    private final Map<Long, Long> ids = new HashMap<>();
    private final Map<Map<Long, Long>, Integer> sections = new HashMap<>();

    public Sections(List<Section> sections) {
        List<Section> copiedSections = new ArrayList<>(sections);
        for (Section section : copiedSections) {
            ids.put(section.getUpStationId(), section.getDownStationId());
            this.sections.put(ids, section.getDistance());
        }
    }

    public List<Long> getStationIds(long upwardTerminalId, long downwardTerminalId) {
        List<Long> stationIds = new ArrayList<>();
        long upwardId = upwardTerminalId;

        while (ids.containsKey(upwardId)) {
            stationIds.add(upwardId);
            upwardId = ids.get(upwardId);
        }

        stationIds.add(downwardTerminalId);
        return Collections.unmodifiableList(stationIds);
    }

    public boolean isNewStationDownward(Section section) {
        for (Map.Entry<Map<Long, Long>, Integer> entry : sections.entrySet()) {
            if (isNewStationDownward(section, entry)) {
                return true;
            }
        }
        return false;
    }

    public void validateIfPossibleToInsert(Section section, long upwardTerminalId, long downwardTerminalId) {
        validateIfAlreadyExistsInLine(section);
        validateIfBothStationNotExistsInLine(section);
        validateDistance(section, upwardTerminalId, downwardTerminalId);
    }

    private void validateIfAlreadyExistsInLine(Section section) {
        if (isBothStationExistsInLine(section)) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "두 역이 이미 노선에 등록되어 있습니다.");
        }
    }

    private boolean isBothStationExistsInLine(Section section) {
        return isStationExists(section.getUpStationId()) && isStationExists(section.getDownStationId());
    }

    private boolean isStationExists(long stationId) {
        return ids.containsKey(stationId) || ids.containsValue(stationId);
    }

    private void validateIfBothStationNotExistsInLine(Section section) {
        if (!isStationExists(section.getUpStationId()) && !isStationExists(section.getDownStationId())) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "노선에 역들이 존재하지 않습니다.");
        }
    }

    private void validateDistance(Section section, long upwardTerminalId, long downwardTerminalId) {
        if (!isSideInsertion(section, upwardTerminalId, downwardTerminalId)) {
            compare(section);
        }
    }

    private boolean isSideInsertion(Section section, long upwardTerminalId, long downwardTerminalId) {
        if (section.getDownStationId() == upwardTerminalId) {
            return true;
        }
        return section.getUpStationId() == downwardTerminalId;
    }

    private void compare(Section section) {
        for (Map.Entry<Map<Long, Long>, Integer> entry : sections.entrySet()) {
            compareByNewStationDirection(section, entry);
        }
    }

    private void compareByNewStationDirection(Section section, Map.Entry<Map<Long, Long>, Integer> entry) {
        if (isNewStationDownward(section, entry)) {
            compareDistance(section, entry);
        }

        if (isNewStationUpward(section, entry)) {
            compareDistance(section, entry);
        }
    }

    private boolean isNewStationDownward(Section section, Map.Entry<Map<Long, Long>, Integer> entry) {
        return entry.getKey().containsKey(section.getUpStationId());
    }

    private boolean isNewStationUpward(Section section, Map.Entry<Map<Long, Long>, Integer> entry) {
        return entry.getKey().containsValue(section.getDownStationId());
    }

    private void compareDistance(Section section, Map.Entry<Map<Long, Long>, Integer> entry) {
        if (entry.getValue() <= section.getDistance()) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "거리 오류");
        }
    }

    public void validateIfPossibleToDelete() {
        if (this.sections.size() == 1) {
            throw new SubwayException(HttpStatus.BAD_REQUEST, "구간이 하나뿐이므로 삭제 불가능합니다.");
        }
    }
}
