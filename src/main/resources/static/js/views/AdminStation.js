import {
  ERROR_MESSAGE,
  EVENT_TYPE,
  KEY_TYPE,
  STATION_NAME_PATTERN
} from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddButton = document.querySelector("#station-add-btn");

  const isNotValidStationAddEvent = (event) => {
    if (event.target === $stationInput && event.key !== KEY_TYPE.ENTER) {
      return true;
    }
    if (event.target === $stationAddButton && event.type !== EVENT_TYPE.CLICK) {
      return true;
    }
    return false;
  };

  const isNotValidStationName = (stationName) => {
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return true;
    }
    if (!STATION_NAME_PATTERN.test(stationName)) {
      alert(ERROR_MESSAGE.NOT_ALLOWED_CHARACTER);
      return true;
    }
    return false;
  };

  const onAddStationHandler = event => {
    if (isNotValidStationAddEvent(event)) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (isNotValidStationName(stationName)) {
      return;
    }
    api.station
    .create({ "name": stationName })
    .then(res => {
      const id = [...res.headers.get("Location").split("/")].pop();
      const station = {
        "id": id,
        "name": stationName
      }
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
    })
    .catch(error => {
      console.log(error);
      alert(ERROR_MESSAGE[error.message]);
    });
    $stationNameInput.value = "";
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const $parent = event.target.closest(".list-item");
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.station
      .delete($parent.dataset.stationId)
      .then(() => {
        $target.closest(".list-item").remove();
      });
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const initDefaultStations = () => {
    api.station
    .get()
    .then(stations => stations
      .map(station => {
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
      })
    );
  };

  const init = () => {
    initEventListeners();
    initDefaultStations();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
