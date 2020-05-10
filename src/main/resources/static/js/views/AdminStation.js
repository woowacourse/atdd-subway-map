import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = async event => {
    if (event.key !== KEY_TYPE.ENTER) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = {
      name: $stationNameInput.value
    };

    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }

    const newStation = await sendNewStation(stationName);
    $stationNameInput.value = "";
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(newStation.name));
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const sendNewStation = data => {
    return fetch("/stations", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(res => res.json())
        .catch(err => console.log(err))
  }

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const init = () => {
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
