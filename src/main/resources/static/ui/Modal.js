import {EVENT_TYPE} from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");

  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $intervalTimeInput = document.querySelector("#interval-time");
  const $firstTimeInput = document.querySelector("#first-time");
  const $lastTimeInput = document.querySelector("#last-time");

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }

    $subwayLineNameInput.value = "";
    $firstTimeInput.value = "";
    $lastTimeInput.value = "";
    $intervalTimeInput.value = "";
    $subwayLineColorInput.value = "";

    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle
  };
}
