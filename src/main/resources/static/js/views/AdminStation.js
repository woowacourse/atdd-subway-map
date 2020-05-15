import {EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddBtn = document.querySelector("#station-add-btn");

  const initDefaultStations = () => {
    api.station.get()
      .then(data => {
        if (!(data instanceof Error)) {
          return data;
        }
        return;
      })
      .then(data => {
        data.forEach(station => {
          $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
        });
      });
  };

  const onAddStationHandler = event => {
    if (event.key !== KEY_TYPE.ENTER && event.type !== EVENT_TYPE.CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    api.station.create({name: stationName})
      .then(data => {
        $stationNameInput.value = "";
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(data));
      });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const stationId = $target.closest("div").dataset.stationId;
      api.station
        .delete(stationId)
        .then(data => {
          if (!(data instanceof Error)) {
            return data;
          }
          return;
        });
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationAddBtn.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
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
