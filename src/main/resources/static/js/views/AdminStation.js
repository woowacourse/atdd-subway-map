import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE, CLICK_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddButton = document.querySelector(("#station-add-btn"));

  const onAddStationHandler = async event => {
    if (event.key !== KEY_TYPE.ENTER && event.button !== CLICK_TYPE.LEFT_CLICK) {
      return;
    }
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = {
      name: $stationNameInput.value
    }
    if (!stationName.name) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }
    if(stationName.name.includes(" ")) {
      alert(ERROR_MESSAGE.NO_BLANK);
      $stationNameInput.value = "";
      return;
    }
    if(/\d/.test(stationName.name)) {
      alert(ERROR_MESSAGE.NO_NUMERIC);
      $stationNameInput.value = "";
      return;
    }
    if(getStationNames().includes(stationName.name)) {
      alert(ERROR_MESSAGE.NO_DUPLICATED);
      $stationNameInput.value = "";
      return;
    }
    $stationNameInput.value = "";
    api.station.create(stationName).then(() => {
          api.station.get().then(value => {
            const createdStation = value.find(x => x.name === stationName.name);
            $stationList.insertAdjacentHTML("beforeend", listItemTemplate(createdStation));
          })
        }
    );

  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm("삭제?")) {
      const $targetLine = $target.parentNode.parentNode; //TODO 더 좋은 방법이..
      $target.closest(".list-item").remove();
      api.station.delete($targetLine.dataset.stationId).then();
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const getStationNames = () => {
    return Array.from($stationList.childNodes)
        .map(x => x.textContent)
        .map(x => x.trim());
  }

  const initStationNames = async () => {
    const stations = await api.station.get();
    stations.forEach(station => {
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
    })
  }

  const init = () => {
    initEventListeners();
    initStationNames();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();