import {EVENT_TYPE} from "./constants.js";

export const replaceEventListener = (target, before, after) => {
    target.removeEventListener(EVENT_TYPE.CLICK, before);
    target.addEventListener(EVENT_TYPE.CLICK, after);
}