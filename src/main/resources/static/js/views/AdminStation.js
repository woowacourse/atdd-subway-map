import { ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import Api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationAddBtn = document.querySelector("#station-add-btn");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }

    const stationRequest = {
      name: $stationNameInput.value
    }

    Api.station.create(stationRequest)
    .then(response => response.json())
    .then(data => {
      const stationResponse = {
        id: data.id,
        name: data.name
      };

      $stationList.insertAdjacentHTML(
        "beforeend",
        listItemTemplate(stationResponse));
    });

    $stationNameInput.value = "";

  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const listItem = $target.closest(".list-item")
      const stationId = listItem.querySelector(".station-id").textContent;

      Api.station.delete(stationId);
      listItem.remove();
    }
    }
  ;

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    }
  ;

  const init = () => {
    initEventListeners();
    }
  ;

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
