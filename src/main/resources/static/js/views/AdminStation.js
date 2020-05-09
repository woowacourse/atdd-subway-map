import {CONFIRM_MESSAGE, ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE, REGEX_PATTERN} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $addStationButton = document.querySelector("#station-add-btn");
  let stationNames = [];

  const isInValidStation = stationName => {
    const invalidConditions = {
      empty : {
        condition : name => !name,
        message : ERROR_MESSAGE.NOT_EMPTY
      },
      containsNumber : {
        condition : name => REGEX_PATTERN.INTEGER.exec(name),
        message : ERROR_MESSAGE.NOT_INTEGER
      },
      containsSpace : {
        condition : name => REGEX_PATTERN.SPACE.exec(name),
        message : ERROR_MESSAGE.NOT_WHITE_SPACE
      },
      duplicateName : {
        condition : name => stationNames.includes(name),
        message : ERROR_MESSAGE.ALREADY_EXIST_STATION
      }
    };
    const firstInvalidObject = Object.values(invalidConditions)
        .find(val => val.condition(stationName));

    if (firstInvalidObject) {
      alert(firstInvalidObject.message);
    }
    return firstInvalidObject;
  };

  const onAddStationHandler = async event => {
    if (event.type === EVENT_TYPE.KEY_PRESS && event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (isInValidStation(stationName)) {
      return;
    }
    const savedStation = await api.station.create({name : stationName});
    stationNames = [...stationNames, savedStation];
    $stationNameInput.value = "";
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationName));
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm(CONFIRM_MESSAGE.DELETE)) {
      const targetName = $target.closest(".list-item").innerText;
      const targetId = stationNames.find(station => station.name === targetName)["id"];
      await api.station.delete(targetId);
      $target.closest(".list-item").remove();
      const index = stationNames.map(subway => subway["id"])
          .indexOf(targetId);
      stationNames.splice(index, 1);
    }
  };

  const initDefaultSubwayLines = async () => {
    stationNames = await api.station.get();
    stationNames.map(station => {
      $stationList.insertAdjacentHTML(
          "beforeend",
          listItemTemplate(station.name)
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
