import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle
  };
}
