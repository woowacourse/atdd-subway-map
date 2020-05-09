import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddButton = document.querySelector("#station-add-btn");

  const state = {
    stations: []
  };

  const validate = station => {
    if (!station) {
      throw ERROR_MESSAGE.NOT_EMPTY;
    }
    if (station.includes(" ")) {
      throw ERROR_MESSAGE.NOT_BLANK;
    }
    if (station.match(/[0-9]/)) {
      throw ERROR_MESSAGE.NOT_NUMBER;
    }
    if (state.stations.includes(station)) {
      throw ERROR_MESSAGE.NOT_EXISTS;
    }
  };

  const addStation = station => {
    state.stations = state.stations.concat(station);
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
  };

  const removeStation = station => {
    state.stations = state.stations.filter(value => value !== station);
    [...$stationList.querySelectorAll('.list-item')].find($station => $station.innerText === station).remove();
  };

  const onAddStationHandler = async event => {
    if (event.type === EVENT_TYPE.KEY_PRESS && event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    try {
      validate(stationName);
      const { id } = await api.station.create({ name: stationName });
      addStation({ id, name: stationName });
      $stationNameInput.value = "";
    } catch (e) {
      alert(e);
    }
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm('정말로 삭제하시겠습니까?')) {
      const stationName = $target.closest(".list-item").innerText;
      const station = state.stations.find(({name}) => name === stationName);
      await api.station.delete(station.id);
      removeStation($target.closest(".list-item").innerText);
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const init = async () => {
    state.stations = await api.station.get();
    $stationList.innerHTML = state.stations.map(listItemTemplate).join("");
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
