import { EVENT_TYPE, ERROR_MESSAGE, KEY_TYPE } from "../../utils/constants.js";
import { listItemTemplate } from "../../utils/templates.js";
import api from "../../api/index.js";

function getPureContents(contents) {
  contents = contents.replace(/\s+/, "");
  contents = contents.replace(/\s+$/g, "");
  contents = contents.replace(/\n/g, "");
  contents = contents.replace(/\r/g, "");
  return contents;
}

function AdminStation() {
  const $stationAddButton = document.querySelector("#station-add-btn");
  const $stationInput = document.querySelector("#station-name");
  const $stationList = document.querySelector("#station-list");

  const initDefaultSubwayStations = () => {
    const stations = api.station.get();
    stations.then(data => data.map(station => {
        $stationList.insertAdjacentHTML("beforeend", listItemTemplate(station));
    }))
  };

  const onAddStationHandler = event => {
    if (event.type !== 'click' && event.key !== KEY_TYPE.ENTER) {
        return;
    }

    event.preventDefault();
    const $stationNameInput = document.querySelector("#station-name");
    const stationName = $stationNameInput.value;

    if (!stationName) {
      alert(ERROR_MESSAGE.NOT_EMPTY);
      return;
    }

    if (stationName.search(/\s/) != -1) {
      alert(ERROR_MESSAGE.NOT_SPACE);
      return;
    }

    var regExp = /[0-9]/;
    if (regExp.test(stationName)) {
      alert(ERROR_MESSAGE.NOT_NUMBER);
      return;
    }

    var stationList = $stationList.childNodes;
    for (var i = 0; i < stationList.length; i++) {
      if(getPureContents(stationList[i].textContent) == stationName){
        alert(ERROR_MESSAGE.NOT_DUPLICATE);
        return;
      }
    };

    const stationData = {
      name: $stationNameInput.value
    };
    api.station.create(stationData).then(() => {
      $stationNameInput.value = "";
      $stationList.innerHTML = "";
      initDefaultSubwayStations();
    });
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    const $targetParent = $target.closest(".list-item");
    const id = $targetParent.dataset.subwayId;
    if (isDeleteButton) {
      if(confirm("정말로 삭제하시겠습니까?")){
        $targetParent.remove();
        api.station.delete(id);
      }
    }
  };

  const initEventListeners = () => {
    $stationAddButton.addEventListener('click', onAddStationHandler);
    $stationInput.addEventListener(EVENT_TYPE.KEY_PRESS, onAddStationHandler);
    $stationList.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const init = () => {
    initDefaultSubwayStations();
    initEventListeners();
  };

  return {
    init
  };
}

const adminStation = new AdminStation();
adminStation.init();
