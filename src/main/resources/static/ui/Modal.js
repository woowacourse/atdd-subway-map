import { EVENT_TYPE } from "../utils/constants.js";

export default function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");
  const $subwaySubmitButton = document.querySelector("#submit-button");

  const toggle = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
    const hasClass = $subwaySubmitButton.classList.contains('update-submit-button');
    if (hasClass) {
      $subwaySubmitButton.classList.remove('update-submit-button');
    }
  };

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

  return {
    toggle
  };
}
