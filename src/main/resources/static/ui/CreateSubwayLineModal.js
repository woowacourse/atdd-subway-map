import { EVENT_TYPE, ERROR_MESSAGE } from "../utils/constants.js";
import { colorSelectOptionTemplate } from "../utils/templates.js";
import { subwayLineColorOptions } from "../utils/defaultSubwayData.js";
import EventEmitter from "../utils/eventEmitter.js";
import Modal from "./Modal.js"

function CreateSubwayLineModal() {
  const $subwayLineNameInput = document.querySelector("#subway-line-name");
  const $subwayLineColorInput = document.querySelector("#subway-line-color");
  const $subwayLineFirstTimeInput = document.querySelector("#first-time");
  const $subwayLineLastTimeInput = document.querySelector("#last-time");
  const $subwayLineIntervalTimeInput = document.querySelector("#interval-time");

  const $createSubwayLineButton = document.querySelector(
    "#subway-line-create-form #submit-button"
  );

  const subwayLineModal = new Modal();

  const eventBus = EventEmitter();

  const validate = () => {
    if (!$subwayLineNameInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$subwayLineColorInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$subwayLineFirstTimeInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$subwayLineLastTimeInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$subwayLineIntervalTimeInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
  };

  const clear = () => {
    $subwayLineNameInput.value = "";
    $subwayLineColorInput.value = "";
    $subwayLineFirstTimeInput.value = "";
    $subwayLineLastTimeInput.value = "";
    $subwayLineIntervalTimeInput.value = "";
  };

  const json = () => {
    return {
      name: $subwayLineNameInput.value,
      color: $subwayLineColorInput.value,
      startTime: $subwayLineFirstTimeInput.value,
      endTime: $subwayLineLastTimeInput.value,
      intervalTime: $subwayLineIntervalTimeInput.value
    };
  };

  const setData = data => {
    $subwayLineNameInput.value = data.name;
    $subwayLineColorInput.value = data.color;
    $subwayLineFirstTimeInput.value = data.startTime;
    $subwayLineLastTimeInput.value = data.endTime;
    $subwayLineIntervalTimeInput.value = data.intervalTime;
  };

  const toggle = () => subwayLineModal.toggle();

  const onSelectColorHandler = event => {
    event.preventDefault();
    const $target = event.target;
    if ($target.classList.contains("color-select-option")) {
      document.querySelector("#subway-line-color").value =
        $target.dataset.color;
    }
  };

  const initCreateSubwayLineForm = () => {
    const $colorSelectContainer = document.querySelector(
      "#subway-line-color-select-container"
    );
    const colorSelectTemplate = subwayLineColorOptions
      .map((option, index) => colorSelectOptionTemplate(option, index))
      .join("");
    $colorSelectContainer.insertAdjacentHTML("beforeend", colorSelectTemplate);
    $colorSelectContainer.addEventListener(
      EVENT_TYPE.CLICK,
      onSelectColorHandler
    );
  };

  const initEventListeners = () => {
    $createSubwayLineButton.addEventListener(EVENT_TYPE.CLICK, event => {
      event.preventDefault();
      try {
        const data = json();
        validate(data)
        eventBus.emit("submit", data);
      } catch (e) {
        alert(e);
      }
    })
  };

  const init = () => {
    initCreateSubwayLineForm();
    initEventListeners();
  };

  return {
    init,
    json,
    clear,
    setData,
    toggle,
    ...eventBus
  };
}

export default CreateSubwayLineModal;