import { EVENT_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import { validateSubwayName } from '../../utils/validate.js';
import api from '../../api/index.js';

function AdminStation() {
  let stations = [];
  const $stationAddForm = document.querySelector("#station-add-form");
  const $stationList = document.querySelector("#station-list");

  const onAddStationHandler = event => {
    try {
      event.preventDefault();
      const $stationNameInput = document.querySelector("#station-name");
      const stationName = $stationNameInput.value;
      validateSubwayName(stationName, stations);
      api.station.create({ name: stationName })
      .then(response => {
        if (response.status !== 201) {
          throw new Error("잘못된 요청입니다.");
        }
        return response.json();
      }).then(station => {
        $stationNameInput.value = "";
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
        stations = [...stations, station]
      }).catch(error => new Error(error.message));
    }
    catch (error) {
      alert(error.message);
    }
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton && confirm("진짜 지울거야?")) {
      const stationItem = $target.closest(".list-item");
      const id = stationItem.dataset.stationId;
      api.station.delete(id)
      .then(response => {
        if (response.status !== 204) {
          throw new Error("삭제 실패");
        }
        stations = stations.filter(station => station.id !== id);
        stationItem.remove();
      }).catch(error => alert(error.message));
    }
  };

  const initStations = () => {
    api.station.getAll()
    .then(response => {
      if (response.status !== 200) {
        throw new Error("잘못된 요청입니다.");
      }
      return response.json();
    }).then(fetchedStations => {
      stations = [...fetchedStations];
      return fetchedStations.map(station => listItemTemplate(station))
      .join("");
    }).then(stationsTemplate => $stationList.innerHTML = stationsTemplate)
    .catch(error => alert(error.message));
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
