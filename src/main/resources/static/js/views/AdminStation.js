import { CONFIRM_MESSAGE, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationButton = document.querySelector("#station-add-btn");

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.key !== KEY_TYPE.CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (isValidStationName(stationName)) {
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
    }
    $stationNameInput.value = "";
  };

  const isValidStationName = stationName => {
    if (!stationName || stationName === "") {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return false;
    }
    if (stationName.includes(" ")) {
      alert(ERROR_MESSAGE.NOT_BLANK);
      return false;
    }
    for (let s of stationName) {
      if (!isNaN(parseInt(s))) {
        alert(ERROR_MESSAGE.NOT_NUMBER);
        return false;
      }
    }
    if ($stationList.innerText.includes(stationName)) {
      alert(ERROR_MESSAGE.NOT_DUPLICATED);
      return false;
    }
    return true;
  };

  const onRemoveStationHandler = event => {
    if (confirm(CONFIRM_MESSAGE.DELETE)) {
      const $target = event.target;
      const isDeleteButton = $target.classList.contains("mdi-delete");
      if (isDeleteButton) {
        $target.closest(".list-item").remove();
      }
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const init = () => {
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
