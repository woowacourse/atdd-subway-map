import {EVENT_TYPE} from "../utils/constants.js";

export default function ModalEdge() {
    const $openModalButton = document.querySelector(".modal-open");
    const $closeModalButton = document.querySelector(".modal-close");
    const $body = document.querySelector("body");
    const $modal = document.querySelector(".modal");
    const $departStation = document.querySelector("#depart-station-name");
    const $arrivalStation = document.querySelector("#arrival-station-name");

    const toggle = event => {
        if (event) {
            event.preventDefault();
        }
        $body.classList.toggle("modal-active");
        $modal.classList.toggle("opacity-0");
        $modal.classList.toggle("pointer-events-none");
        $departStation.value = "";
        $arrivalStation.value = "";
    };

    $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
    $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

    return {toggle};
}
