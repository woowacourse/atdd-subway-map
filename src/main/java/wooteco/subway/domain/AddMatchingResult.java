package wooteco.subway.domain;

public enum AddMatchingResult {

    ADD_TO_RIGHT,
    ADD_TO_LEFT,
    SAME_SECTION,
    NO_MATCHED
    ;

    public static AddMatchingResult matchMiddleStation(final Section exSection, final Section newSection) {
        if (exSection.isSameSection(newSection)) {
            return SAME_SECTION;
        }
        if (exSection.isSameDownStation(newSection)) {
            return ADD_TO_LEFT;
        }
        if (exSection.isSameUpStation(newSection)) {
            return ADD_TO_RIGHT;
        }
        return NO_MATCHED;
    }

    public static AddMatchingResult matchStartStation(final Section exSection, final Section newSection) {
        if (exSection.isUpStationSameAsDownStation(newSection)) {
            return AddMatchingResult.ADD_TO_LEFT;
        }
        return AddMatchingResult.NO_MATCHED;
    }

    public static AddMatchingResult matchEndStation(final Section exSection, final Section newSection) {
        if (exSection.isDownStationSameAsUpStation(newSection)) {
            return AddMatchingResult.ADD_TO_RIGHT;
        }
        return AddMatchingResult.NO_MATCHED;
    }
}
