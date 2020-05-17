import {ERROR_MESSAGE, EVENT_TYPE, KEY_TYPE} from "../../utils/constants.js";
import {listItemTemplate} from "../../utils/templates.js";
import api from "../../api/index.js";

function AdminStation() {
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");
  const $stationAddButton = document.querySelector('#station-add-btn');

  const onAddStationHandler = event => {
    if (event.type !== EVENT_TYPE.CLICK && event.key !== KEY_TYPE.ENTER) {
      return;
    }

    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;

    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }

    const blankPatten = /\s/g;
    if (stationName.match(blankPatten)) {
      alert(ERROR_MESSAGE.NOT_SPACE);
      return;
    }

    const numberPattern = /\d/g;
    if (stationName.match(numberPattern)) {
      alert(ERROR_MESSAGE.NOT_NUMBER);
      return;
    }

    let isDuplicate = false;
    $stationList.childNodes.forEach(item => {
      if (item.textContent.trim() === stationName) {
        isDuplicate = true;
      }
    });
    if (isDuplicate) {
      alert(ERROR_MESSAGE.NOT_DUPLICATE);
      return;
    }

    $stationNameInput.value = "";
    api.station.create({
      name: stationName
    }).then(station => {
      if (!station.name) {
        return;
      }
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate({
        id: station.id,
        name: station.name
      }));
    });
    alert(stationName + '역 추가!');
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm("정말로 삭제할거에요?")) {
      const $stationListItem = $target.closest(".list-item");
      api.station.delete($stationListItem.dataset.stationId).then(() => {
        $stationListItem.remove();
      });
    }
  };

  const initEventListeners = () => {
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $stationAddButton.addEventListener(EVENT_TYPE.CLICK, onAddStationHandler);
  };

  const initStations = () => {
    api.station.get().then(stations => {
      stations.map(station => {
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate({
          id: station.id,
          name: station.name
        }));
      });
    });
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
