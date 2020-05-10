import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $createLineStationButton = document.querySelector("#subway-line-add-button");
  const $submitLineStationButton = document.querySelector("#submit-button")
  const $subwayLineSelection = document.querySelector('#station-select-options');
  const $subwayDepartStation = document.querySelector('#depart-station-name');
  const $subwayArrivalStation = document.querySelector('#arrival-station-name');
  const $subwayDistance = document.querySelector("#distance");
  const $subwayDuration = document.querySelector("#duration");

  const createSubwayEdgeModal = new Modal();

  let subwayLines = [];

  const initSubwayLinesSlider = () => {

    // let hello = [];
    // api.line.get().then(data => data.map(line => hello.push(subwayLinesItemTemplate(line))));
    //
    // console.log(hello);
    // console.log("###");
    // console.log(hello[0]);
    // console.log(hello.length);
    // console.log(hello.join(""));

    $subwayLinesSlider.innerHTML = subwayLines.map(line => subwayLinesItemTemplate(line)).join("");

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
    api.line.get().then(data => console.log(data));
    const subwayLineOptionTemplate =
      api.line.get()
      .then(data => data.map(line => optionTemplate(line)))
      .then(data => data.join(""));
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  /* 구간 추가 버튼 */
  const onAddLineStationHandler = event => {
    event.preventDefault();
    const data = {
      lineSelection: $subwayLineSelection.value,
      departStation: $subwayDepartStation.value,
      arrivalStation: $subwayArrivalStation.value,
      distance: $subwayDistance.value,
      duration: $subwayDuration.value
    };
    console.log(data);
    if ($submitLineStationButton) {
      alert("");
    }
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $createLineStationButton.addEventListener(EVENT_TYPE.CLICK, onAddLineStationHandler);
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
  };

  const initState = async () => {
    subwayLines = await api.line.get();
  };

  this.init = async () => {
    await initState();
    initEventListeners();
    initSubwayLinesSlider();
    initSubwayLineOptions();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
