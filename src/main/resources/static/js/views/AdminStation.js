import { CONFIRM_MESSAGE, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from '../../api/index.js';

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationButton = document.querySelector("#station-add-btn");

  const initStations = () => {
    api.station.get().then((stations) => {
      stations.map(st =>
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(st.name, st.id)));
    });
  }

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.key !== KEY_TYPE.CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (isValidStationName(stationName)) {
      const data = {
        name: stationName
      };
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
      api.station.create(data)
      .then(() => $stationNameInput.value = "");
    }
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
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm(CONFIRM_MESSAGE.DELETE)) {
      const id = $target.closest(".list-item").dataset.stationId;
      $target.closest(".list-item").remove();
      api.station.delete(id);
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const init = () => {
    initEventListeners();
    initStations();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
