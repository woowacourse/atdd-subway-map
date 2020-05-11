import {
  listItemTemplate,
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  let lines = [];
  let stations = [];
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $submitButton = document.querySelector("#submit-button");
  const createSubwayEdgeModal = new Modal();

  const initializeStations = () => {
    api.station.getAll()
    .then(response => {
      if (response.status !== 200) {
        throw new Error("잘못된 요청입니다.");
      }
      return response.json();
    }).then(fetchedStations => stations = [...fetchedStations])
    .catch(error => alert(error.message));
  }

  const initSubwayLinesSlider = () => {
    api.line.getAll()
    .then(response => {
      if (response.status !== 200) {
        throw new Error("잘못된 요청입니다.");
      }
      return response.json();
    }).then(fetchedLines => {
      lines = [...fetchedLines];
      return lines.map(line => subwayLinesItemTemplate(line))
      .join("");
    }).then(linesTemplate => $subwayLinesSlider.innerHTML = linesTemplate)
    .then(() => {
      tns({
        container: ".subway-lines-slider",
        loop: true,
        slideBy: "page",
        speed: 400,
        autoplayButtonOutput: false,
        mouseDrag: true,
        lazyload: true,
        controlsContainer: "#slider-controls",
        items: 1,
        edgePadding: 25
      });
      initSubwayLineOptions();
    })
    .catch(error => alert(error.message));
  };

  const initSubwayLineOptions = () => {
    const subwayLineOptionTemplate = lines
    .map(line => optionTemplate(line.id, line.name))
    .join("");
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const $stationContainer = $target.closest(".station-container");
      const $item = $target.closest(".list-item")
      const lineId = $stationContainer.dataset.lineId;
      const stationId = $item.dataset.stationId;
      api.lineStation.delete(lineId, stationId)
      .then(response => {
        if (response.status !== 204) {
          throw new Error("잘못된 요청입니다.");
        }
        const line = lines.find(line => line.id === parseInt(lineId));
        line.stations = line.stations.filter(station => station.id !== parseInt(stationId))
        lines = [...lines];
        console.log(lines);
        $item.remove();
      }).catch(error => alert(error.message));
    }
  };

  const onSubmitHandler = event => {
    event.preventDefault();
    const $target = event.target;
    const isSubmitButton = $target.id === "submit-button";
    if (isSubmitButton) {
      const $departStationName = document.querySelector("#depart-station-name").value.trim();
      const $arrivalStationName = document.querySelector("#arrival-station-name").value.trim();
      const $lineSelect = document.querySelector("#station-select-options");
      const preStation = stations.find(station => station.name === $departStationName);
      const station = stations.find(station => station.name === $arrivalStationName);
      const lineId = $lineSelect[$lineSelect.selectedIndex].dataset.lineId;
      const lineStationRequest = {
        preStationId: preStation ? preStation.id : null,
        stationId: station.id,
        distance: 10,
        duration: 20,
      }
      api.lineStation.create(lineId, lineStationRequest)
      .then(response => {
        if (response.status !== 201) {
          throw new Error("잘못된 요청입니다.");
        }
      }).then(() => {
        const line = lines.find(line => line.id === parseInt(lineId));
        const template = listItemTemplate(station);
        if (preStation) {
          const $preStationItem = document.querySelectorAll(`.station-${preStation.id}`);
          $preStationItem.forEach(item => item.insertAdjacentHTML("afterend", template))
        } else {
          const $stationItemList = document.querySelectorAll(`.station-container-${line.id}`)
          $stationItemList.forEach(item => item.insertAdjacentHTML("afterbegin", template))
        }
        line.stations = [...line.stations, station];
        lines = [...lines];
        createSubwayEdgeModal.toggle();
      }).catch(error => alert(error.message));
    }
  }

  const initEventListeners = () => {
    $submitButton.addEventListener(EVENT_TYPE.CLICK, onSubmitHandler);
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
  };

  this.init = () => {
    initSubwayLinesSlider();
    // initializeStations()
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
