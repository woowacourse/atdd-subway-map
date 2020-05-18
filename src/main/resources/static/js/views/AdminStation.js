import {CONFIRM_MESSAGE, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE, REGEX_PATTERN} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $addStationButton = document.querySelector("#station-add-btn");
  let stations = [];

  const validateStationName = name => {
    if (!name) {
      throw new Error(ERROR_MESSAGE.NOT_EMPTY);
    }
    if (REGEX_PATTERN.INTEGER.exec(name)) {
      throw new Error(ERROR_MESSAGE.NOT_INTEGER);
    }
    if (REGEX_PATTERN.SPACE.exec(name)) {
      throw new Error(ERROR_MESSAGE.NOT_WHITE_SPACE);
    }
    if (stations.find(station => station.name === name)) {
      throw new Error(ERROR_MESSAGE.ALREADY_EXIST_STATION);
    }
  };

  const onAddStationHandler = async event => {
    if (event.type === EVENT_TYPE.KEY_PRESS && event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    try {
      validateStationName(stationName);
      const savedStationId = await api.station.create({name: stationName});
      const addedStation = {name: stationName, id: savedStationId};
      stations = [...stations, addedStation];
      $stationNameInput.value = "";
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(addedStation));
    } catch (e) {
      alert(e.message);
    }
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (!isDeleteButton || !confirm(CONFIRM_MESSAGE.DELETE)) {
      return;
    }
    try {
      const $targetNode = $target.closest(".list-item");
      const targetName = $targetNode.innerText;
      const targetId = $targetNode.dataset.stationId;
      await api.station.delete(targetId);
      const target = stations.find(station => station === {id: targetId, name: targetName});
      stations = stations.filter(station => station !== target)
      $target.closest(".list-item").remove();
    } catch (e) {
      alert(e.message);
    }
  };

  const initDefaultSubwayLines = async () => {
    stations = await api.station.get();
    stations.map(station => {
      $stationList.insertAdjacentHTML(
          "beforeend",
          listItemTemplate(station)
      );
    });
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $addStationButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const init = () => {
    initDefaultSubwayLines().then();
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
