import {EVENT_TYPE} from "../utils/constants.js";

export default function Modal() {
    const $openModalButton = document.querySelector(".modal-open");
    const $closeModalButton = document.querySelector(".modal-close");
    const $body = document.querySelector("body");
    const $modal = document.querySelector(".modal");
    const $subwayLineNameInput = document.getElementById("subway-line-name");
    const $subwayStartTimeInput = document.getElementById("first-time");
    const $subwayEndTimeInput = document.getElementById("last-time");
    const $subwayIntervalTimeInput = document.getElementById("interval-time");
    const $subwayLineBgColorInput = document.getElementById("subway-line-color");

    const toggle = event => {
        if (event) {
            event.preventDefault();
        }
        $body.classList.toggle("modal-active");
        $modal.classList.toggle("opacity-0");
        $modal.classList.toggle("pointer-events-none");
    };

    const clear = () => {
        $subwayLineNameInput.value = "";
        $subwayStartTimeInput.value = "";
        $subwayEndTimeInput.value = "";
        $subwayIntervalTimeInput.value = "";
        $subwayLineBgColorInput.value = "";
    };

    $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
    $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
    $closeModalButton.addEventListener(EVENT_TYPE.CLICK, clear);

    return {
        toggle
    };
}
