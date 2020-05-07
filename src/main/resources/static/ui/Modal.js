import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");
  const $subwayLineName = document.querySelector("#subway-line-name");
  const $subwayFirstTime = document.querySelector("#first-time");
  const $subwayLastTime = document.querySelector("#last-time");
  const $subwayIntervalTime = document.querySelector("#interval-time");
  const $subwayLineColor = document.querySelector("#subway-line-color");
  const $subwayLineId = document.querySelector("#subway-line-id");


  const toggle = (event) => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  const toggleWithInit = (event, line) => {
    replaceInputData(line);
    toggle(event);
  }

  const replaceInputData = (line = {}) => {
    $subwayLineId.value = line.id ? line.id : "";
    $subwayLineName.value = line.name ? line.name : "";
    $subwayFirstTime.value = line.startTime ? line.startTime : "";
    $subwayLastTime.value = line.endTime ? line.endTime : "";
    $subwayIntervalTime.value = line.intervalTime ? line.intervalTime : "";
    $subwayLineColor.value = line.color ? line.color : "";
  }

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggleWithInit);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle,
    toggleWithInit,
  };
}
