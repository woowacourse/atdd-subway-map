import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $openModalButtonByCreate = document.querySelector("#subway-line-add-btn");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");
  const $submitButton = document.querySelector("#submit-button");

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  const toggleCreateButton = () => {
    $submitButton.classList.toggle("create-btn");
  }

  const createLine = event => {
    toggle(event);
    toggleCreateButton();
  }

  // $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
  $openModalButtonByCreate.addEventListener(EVENT_TYPE.CLICK, createLine);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle,
    toggleCreateButton
  };
}
