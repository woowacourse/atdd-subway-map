package wooteco.subway.admin.domain.vo;

public class BgColor {

    private String bgColor;

    public BgColor(String bgColor) {
        validate(bgColor);
        this.bgColor = bgColor;
    }

    private void validate(String bgColor) {
        if (!bgColor.matches("bg-[a-zA-Z]+-\\d{3}")) {
            throw new IllegalArgumentException("Line.bgColor의 형식이 아닙니다. (bg-color-no)");
        }
    }

    public String getBgColor() {
        return bgColor;
    }
}
