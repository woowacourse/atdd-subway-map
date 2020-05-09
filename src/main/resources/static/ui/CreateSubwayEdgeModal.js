import { EVENT_TYPE, ERROR_MESSAGE } from "../utils/constants.js";
import { optionTemplate, colorSelectOptionTemplate } from "../utils/templates.js";
import { subwayLineColorOptions } from "../utils/defaultSubwayData.js";
import EventEmitter from "../utils/eventEmitter.js";
import Modal from "./Modal.js"

function CreateSubwayEdgeModal() {
  const $stationSelectOptions = document.querySelector("#station-select-options");
  const $departStationNameInput = document.querySelector("#depart-station-name");
  const $arrivalStationNameInput = document.querySelector("#arrival-station-name");
  const $createSubwayLineButton = document.querySelector("#submit-button");

  const subwayEdgeModal = new Modal();

  const eventBus = EventEmitter();

  const validate = () => {
    if (!$stationSelectOptions.options[$stationSelectOptions.selectedIndex].value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$departStationNameInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (!$arrivalStationNameInput.value) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
  };

  const clear = () => {
    $stationSelectOptions.selectedIndex = -1;
    $departStationNameInput.value = "";
    $arrivalStationNameInput.value = "";
  };

  const json = () => {
    return {
      lineId: $stationSelectOptions.options[$stationSelectOptions.selectedIndex].value,
      preStation: $departStationNameInput.value,
      station: $arrivalStationNameInput.value,

    };
  };

  const toggle = () => subwayEdgeModal.toggle();

  const initSubwayLineOptions = lines => {
    const subwayLineOptionTemplate = lines.map(line => optionTemplate(line)).join("");
    const $stationSelectOptions = document.querySelector("#station-select-options");
    $stationSelectOptions.insertAdjacentHTML(
        "afterbegin",
        subwayLineOptionTemplate
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

  const init = lines => {
    initSubwayLineOptions(lines);
    initEventListeners();
  };

  return {
    init,
    json,
    clear,
    toggle,
    ...eventBus
  };
}

export default CreateSubwayEdgeModal;