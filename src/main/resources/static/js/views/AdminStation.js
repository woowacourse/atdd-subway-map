import { EVENT_TYPE, ERROR_MESSAGE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";

function AdminStation() {
  const $stationAddButton = document.querySelector("#station-add-btn");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = event => {
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!stationName) {
      Snackbar.show({
        text: ERROR_MESSAGE.NOT_EMPTY,
        pos: "bottom-center",
        showAction: false,
        duration: 2000
      });
      return;
    }
    $stationNameInput.value = "";
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  this.init = () => {
    initEventListeners();
  };
}

const adminStation = new AdminStation();
adminStation.init();
