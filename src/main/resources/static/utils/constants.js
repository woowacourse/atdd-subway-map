export const EVENT_TYPE = {
    CLICK: "click",
    KEY_PRESS: "keypress"
};

export const ERROR_MESSAGE = {
    NOT_EMPTY: "🤔 값을 입력해주세요",
    DUPLICATED: "🤔 동일한 이름의 역이 존재합니다. 다시 입력해주세요.",
    NOT_ALLOWED_CHARACTER: "🤔 공백과 숫자는 입력하실 수 없습니다. 다시 입력해주세요."
};

export const KEY_TYPE = {
    ENTER: "Enter"
};

export const STATION_NAME_PATTERN = new RegExp(/^[^\s]+$/);