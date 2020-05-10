import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $addSubwayEdgeButton = document.querySelector("#submit-button");
  const $selectStationInput = document.querySelector("#station-select-options");
  const $departStationInput = document.querySelector("#depart-station-name");
  const $arriveStationInput = document.querySelector("#arrival-station-name");

  const createSubwayEdgeModal = new Modal();

  let stations = [];
  let lines = [];

  const initSubwayLinesSlider = async () => {
    const subwayLines = await api.line.get();
    lines = subwayLines;
    stations = await api.station.get();
    $subwayLinesSlider.innerHTML = subwayLines
      .map(line => subwayLinesItemTemplate(line))
      .join("");
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
  };

  const initSubwayLineOptions = async () => {
    const subwayLines = await api.line.get().then();
    const subwayLineOptionTemplate = subwayLines
      .map(line => optionTemplate(line.name))
      .join("");
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  const onCreateStationHandler = async event => {
    event.preventDefault();
    const selectLineName = $selectStationInput.value;
    const selectDepartStation = $departStationInput.value;
    const selectArriveStation = $arriveStationInput.value;

    const lineId = lines.find(line => line.name === selectLineName)["id"];
    const arriveStationId = stations.find(station => station.name === selectArriveStation)["id"];
    const departStationId = selectDepartStation === "" ? null : stations.find(station => station.name === selectDepartStation)["id"];
    if (!lineId || departStationId === undefined || !arriveStationId) {
      return;
    }
    const requestData = {
      preStationId : departStationId,
      stationId : arriveStationId,
      distance : 10,
      duration : 10
    };

    await api.edge.create(lineId, requestData);

    initSubwayLinesSlider().then();
    createSubwayEdgeModal.toggle();
    cleanComponent();
  };

  const cleanComponent = () => {
    $selectStationInput.value = "";
    $departStationInput.value = "";
    $arriveStationInput.value = "";
  };

  const onRemoveStationHandler = async event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      let lineName = $target.parentNode.parentNode.parentNode.parentNode.innerText.split('\n')[0];
      let stationName = $target.parentNode.parentNode.innerText;
      const lineId = lines.find(line => line.name === lineName)["id"];
      const deleteStationId = stations.find(station => station.name === stationName)["id"];
      await api.edge.delete(lineId, deleteStationId);
      $target.closest(".list-item").remove();

      const lineIndex = lines.findIndex(line => line.id === lineId);
      const stationIndex = lines[lineIndex]["stations"].findIndex(station => station.id === deleteStationId);
      lines[lineIndex]["stations"].splice(stationIndex, 1);
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $addSubwayEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateStationHandler);
  };

  this.init = () => {
    initSubwayLinesSlider().then();
    initSubwayLineOptions().then();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
