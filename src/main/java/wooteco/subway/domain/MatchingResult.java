package wooteco.subway.domain;

public enum MatchingResult {
    ADD_TO_RIGHT,
    ADD_TO_LEFT,
    SAME_SECTION,
    NO_MATCHED
    ;

    public static MatchingResult matchMiddleStation(final Section exSection, final Section newSection) {
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

    public static MatchingResult matchStartStation(final Section exSection, final Section newSection) {
        if (exSection.isUpStationSameAsDownStation(newSection)) {
            return MatchingResult.ADD_TO_LEFT;
        }
        return MatchingResult.NO_MATCHED;
    }

    public static MatchingResult matchEndStation(final Section exSection, final Section newSection) {
        if (exSection.isDownStationSameAsUpStation(newSection)) {
            return MatchingResult.ADD_TO_RIGHT;
        }
        return MatchingResult.NO_MATCHED;
    }
}
