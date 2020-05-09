import { ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  let stations = [];
  let modifyingStationId = null;

  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddButton = document.querySelector(("#station-add-btn"));

  const onAddStationHandler = event => {
    if (event.type !== EVENT_TYPE.CLICK && event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const name = $stationInput.value.trim();
    if (!name) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }
    api.station.create({ name })
      .then(response => {
        stations = [...stations, response];
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(response));
        $stationInput.value = "";
      });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      modifyingStationId = parseInt($target.closest(".list-item").dataset.stationId);
      api.station.delete(modifyingStationId)
        .then(() => {
          stations = stations.filter(station => station.id !== modifyingStationId);
        })
        .catch(err => {
          console.log(err);
        })
        .finally(() => {
          modifyingStationId = null;
        });
      $target.closest(".list-item").remove();
    }
  };

  const initDefaultStations = () => {
    api.station.getAll()
      .then(data => {
        stations = data;
        stations
          .map(({ name, id }) => ({ name, id }))
          .map(station => {
            $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
          });
      });
  };

  const initEventListeners = () => {
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  this.init = () => {
    initDefaultStations();
    initEventListeners();
  };
}

const adminStation = new AdminStation();
adminStation.init();
