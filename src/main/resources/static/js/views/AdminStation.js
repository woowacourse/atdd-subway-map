import { ERROR_MESSAGE, EVENT_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from '../../api/index.js';

function AdminStation() {
  const $stationList = document.querySelector("#station-list");
  const $addForm = document.querySelector("#add-form");

  const onAddStationHandler = async (event) => {
    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;
    if (!isValidate(stationName)) {
      return;
    }

    const stationResponse = await api.station.create(stationName);
    $stationNameInput.value = "";
    $stationList.insertAdjacentHTML("beforeend", listItemTemplate(stationResponse));
  };

  const isValidate = stationName => {
    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return false;
    }

    if (stationName.includes(" ")) {
      alert(ERROR_MESSAGE.NOT_BLANK);
      return false;
    }

    let matches = stationName.match(/\d+/g);
    if (matches != null) {
      alert(ERROR_MESSAGE.NOT_NUMBER);
      return false;
    }

    const $stations = document.querySelectorAll(".list-item");
    const $stationArr = Array.from($stations);
    const isDuplicate = (element) => element.innerText === stationName;
    if ($stationArr.some(isDuplicate)) {
      alert(ERROR_MESSAGE.NOT_DUPLICATION);
      return false;
    }

    return true;
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const $listItem = $target.closest(".list-item");
    if (isDeleteButton && confirm("정말 삭제하시겠습니까?")) {
      api.station.delete($listItem.dataset.stationId).then(response => {
          if (response.ok) {
            $listItem.remove();
          } else {
            alert(response);
          }
        }
      );
    }
  };

  const initEventListeners = () => {
    $addForm.addEventListener(EVENT_TYPE.SUBMIT, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const initDefaultStations = async () => {
    const initStations = await api.station.get();
    initStations.map(station =>
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station))
    );
  }

  const init = () => {
    initDefaultStations();
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
