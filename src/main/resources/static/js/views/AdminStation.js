import { ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";


function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }
    const data = {
      name: stationName
    }
    api.station.create(data).then(station => {
        $stationNameInput.value = "";
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
      }
    );
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.station.delete($target.closest(".list-item").getAttribute("value")).then(() => {
        $target.closest(".list-item").remove();
      })
    }
  };

  const initDefaultSubwayStations = () => {
    api.station.get().then(data => data.map(station => {
      $stationList.insertAdjacentHTML(
        "beforeend",
        listItemTemplate(station)
      )
    }))
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const init = () => {
    initDefaultSubwayStations();
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
