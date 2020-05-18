import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $addSubwayEdgeSubmitButton = document.querySelector("#submit-button");
  const $selectStationInput = document.querySelector("#station-select-options");
  const $departStationInput = document.querySelector("#depart-station-name");
  const $arriveStationInput = document.querySelector("#arrival-station-name");
  const $addSubwayEdgeButton = document.querySelector("#subway-line-add-btn");
  const createSubwayEdgeModal = new Modal();

  let stations = [];
  let lines = [];

  const initSubwayLinesSlider = async () => {
    lines = await api.line.get();
    stations = await api.station.get();
    $subwayLinesSlider.innerHTML = lines
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

  const initSubwayLineOptions = () => {
    const subwayLineOptionTemplate = lines
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

  const onCreateStationFormHandler = () => {
    $selectStationInput.value = "";
    $departStationInput.value = "";
    $arriveStationInput.value = "";
  }

  const onCreateStationHandler = async event => {
    event.preventDefault();
    const lineName = $selectStationInput.value;
    const preStationName = $departStationInput.value;
    const stationName = $arriveStationInput.value;

    const lineId = lines.find(line => line.name === lineName)["id"];
    const arriveStation = stations.find(station => station.name === stationName);
    const departStation = stations.find(station => station.name === preStationName);
    const arriveStationId = arriveStation ? arriveStation["id"] : undefined;
    const departStationId = preStationName === "" ? null : departStation ? departStation["id"] : undefined;
    if (!lineId || departStationId === undefined || !arriveStationId) {
      alert('유효한 값을 입력해주세요');
      return;
    }
    const requestData = {
      preStationId : departStationId,
      stationId : arriveStationId,
      distance : 10,
      duration : 10
    };

    try {
      await api.edge.create(lineId, requestData);
      await initSubwayLinesSlider();
      createSubwayEdgeModal.toggle();
    } catch(e) {
      alert(e.message);
    }
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
    $addSubwayEdgeSubmitButton.addEventListener(EVENT_TYPE.CLICK, onCreateStationHandler);
    $addSubwayEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateStationFormHandler)
  };

  this.init = async () => {
    await initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
