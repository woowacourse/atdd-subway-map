import { EVENT_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from '../../api/index.js';

function AdminStation() {
  let stations = [];
  const $stationAddForm = document.querySelector("#station-add-form");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = async event => {
    try {
      event.preventDefault();
      const $stationNameInput = document.querySelector("#station-name");
      const stationName = $stationNameInput.value;
      const newStation = { name: stationName }
      const response = await api.station.create(newStation);
      if (response.status !== 201) {
        const error = await response.json();
        throw new Error(error.message);
      }
      newStation.id = await response.json();
      $stationNameInput.value = "";
      $stationList.insertAdjacentHTML("beforeend", listItemTemplate(newStation));
      stations = [...stations, newStation]
    }
    catch (error) {
      alert(error.message);
    }
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (!(isDeleteButton && confirm("진짜 지울거야?"))) {
      return;
    }
    try {
      const stationItem = $target.closest(".list-item");
      const id = stationItem.dataset.stationId;
      const response = await api.station.delete(id);
      if (response.status !== 204) {
        throw new Error("삭제 실패");
      }
      stations = stations.filter(station => station.id !== parseInt(id));
      stationItem.remove();
    }
    catch (error) {
      alert(error.message);
    }
  };

  const initStations = async () => {
    try {
      const response = await api.station.getAll();
      if (response.status !== 200) {
        throw new Error("역 정보를 불러올 수 없습니다.");
      }
      const fetchedStations = await response.json();
      $stationList.innerHTML = fetchedStations.map(station => listItemTemplate(station)).join("");
      stations = [...fetchedStations];
    }
    catch (error) {
      alert(error.message);
    }
  }

  const initEventListeners = () => {
    $stationAddForm.addEventListener(EVENT_TYPE.SUBMIT, onAddStationHandler);
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
