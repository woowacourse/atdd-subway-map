import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");
  let $currentLine = null;

  const addCurrentLineItem = ($lineItem) => {
    $currentLine = $lineItem;
  }

  const getCurrentLineItem = () => {
    return $currentLine;
  }

  const addUpdateToClassList = () => {
    $modal.classList.add("update");
  };

  const removeUpdateFromClassList = () => {
    $modal.classList.remove("update");
  };

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle,
    addUpdateToClassList,
    removeUpdateFromClassList,
    addCurrentLineItem,
    getCurrentLineItem
  };
}
