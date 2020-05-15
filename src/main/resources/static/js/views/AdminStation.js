import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import { api } from "../../api/index.js"

function AdminStation() {
  const $stationAddButton = document.querySelector("#station-add-btn");
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandlerWhenEnterPress = event => {
    if (event.key !== KEY_TYPE.ENTER) {
      return;
    }
    onAddStationHandler(event);
  };

  const onAddStationHandler = event => {
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }
    $stationNameInput.value = "";
    let data = {
      name: stationName
    };
    api.station.create(data)
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      console.log($target.parentElement.parentElement.innerText)
      api.station.delete($target.parentElement.parentElement.innerText)
      $target.closest(".list-item").remove();
    }
  };

  const initStations = async () => {
    const stations =  await api.station.get();
    stations.forEach(station => {
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station.name))
    });
  };

  const initEventListeners = () => {
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandlerWhenEnterPress);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
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
