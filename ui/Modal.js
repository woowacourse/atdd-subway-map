import { EVENT_TYPE } from "../utils/constants.js";

function Modal() {
  const $openModalButton = document.querySelector(".modal-open");
  const $closeModalButton = document.querySelector(".modal-close");
  const $body = document.querySelector("body");
  const $modal = document.querySelector(".modal");

  const toggleModal = event => {
    if (event) {
      event.preventDefault();
    }
    $body.classList.toggle("modal-active");
    $modal.classList.toggle("opacity-0");
    $modal.classList.toggle("pointer-events-none");
  };

  $openModalButton.addEventListener(EVENT_TYPE.CLICK, toggleModal);
  $closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggleModal);

  return {
    toggleModal
  };
}

export default Modal;
