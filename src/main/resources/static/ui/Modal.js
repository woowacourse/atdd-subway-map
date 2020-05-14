import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");

  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineStartTimeInput = document.querySelector("#first-time");
  const $subwayLineEndTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");

  const clearForm = () => {
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
    $subwayLineStartTimeInput.value = "";
    $subwayLineEndTimeInput.value = "";
    $subwayLineIntervalTimeInput.value = "";
  };

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
    clearForm();
  };

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle
  };
}
