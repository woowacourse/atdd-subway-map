import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE, CONSTANT } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationButton = document.querySelector("#station-add-btn");

  let stations = [];

  const onAddStationHandler = async event => {
    if (event.key !== KEY_TYPE.ENTER) {
      return;
    }
    await addStation(event);
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const stationName = event.target.parentNode.parentNode.innerText;
    const isConfirmDelete = confirm(`${stationName}를 삭제하겠습니까?`);
    if (isDeleteButton && isConfirmDelete) {
      const targetStationId = stations.find(station => station["name"] === stationName)["id"];
      $target.closest(".list-item").remove();
      await api.station.delete(targetStationId);
      stations.splice(stations.indexOf(stationName), 1);
    }
  };

  function splitStationName(stationName) {
    return stationName.split(CONSTANT.EMPTY);
  }

  async function addStation(event) {
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }
    const names = splitStationName(stationName);
    if (validateBlank($stationNameInput, names)
        && validateNumber($stationNameInput, names)
        && validateDuplicateStationName($stationNameInput, stationName)) {
      const savedStation = await api.station.create(stationName);
      stations = [...stations, savedStation];
      $stationNameInput.value = CONSTANT.EMPTY;
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
    }
  }

  function validateBlank(stationNameInput, splitedStationName) {
    for (const string of splitedStationName) {
      if (string === CONSTANT.BLANK) {
        alert(ERROR_MESSAGE.NOT_BLANK);
        stationNameInput.value = CONSTANT.EMPTY;
        return false;
      }
    }
    return true;
  }

  function validateNumber(stationNameInput, splitedStationName) {
    for (const string of splitedStationName) {
      if (isNumber(string)) {
        alert(ERROR_MESSAGE.IS_NUMBER);
        stationNameInput.value = CONSTANT.EMPTY;
        return false;
      }
    }
    return true;
  }

  function isNumber(string) {
    const result = string / 1;
    return !isNaN(result);
  }

  function validateDuplicateStationName(stationNameInput, input) {
    if (stations.map(station => station["name"]).includes(input)) {
      alert(ERROR_MESSAGE.DUPLICATE);
      stationNameInput.value = CONSTANT.EMPTY;
      return false;
    }
    return true;
  }

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationButton.addEventListener(EVENT_TYPE.CLICK, addStation);
  };

  async function initDefaultStations() {
    stations = await api.station.get();
    stations.map(station => {
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station["name"]));
    });
  }

  const init = async () => {
    await initDefaultStations();
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
