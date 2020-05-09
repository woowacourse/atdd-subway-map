import {EVENT_TYPE} from "../utils/constants.js";

export default function Modal() {
    const $openModalButton = document.querySelector(".modal-open");
    const $closeModalButton = document.querySelector(".modal-close");
    const $body = document.querySelector("body");
    const $modal = document.querySelector(".modal");
    const $submitButton = document.querySelector('#submit-button');

    const toggle = event => {
        if (event) {
            event.preventDefault();
        }
        $body.classList.toggle("modal-active");
        $modal.classList.toggle("opacity-0");
        $modal.classList.toggle("pointer-events-none");
        let hasSubwayLineAddButton = $submitButton.classList.contains('subway-line-add-button');
        if (hasSubwayLineAddButton) {
            $submitButton.classList.remove('subway-line-add-button');
        }
    };

    $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
    $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

    return {
        toggle
    };
}
