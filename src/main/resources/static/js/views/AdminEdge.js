import { optionTemplate, subwayLinesItemTemplate } from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import api from "../../api/index.js";
import Modal from "../../ui/Modal.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $submitCreateEdgeButton = document.querySelector(
    "#subway-line-station-create-form #submit-button");
  const $subwayLineSelection = document.querySelector('#station-select-options');
  const $subwayDepartStation = document.querySelector('#depart-station-name');
  const $subwayArrivalStation = document.querySelector('#arrival-station-name');
  const $subwayDistance = document.querySelector("#distance");
  const $subwayDuration = document.querySelector("#duration");

  const createSubwayEdgeModal = new Modal();

  let subwayLines = [];

  const initSubwayLinesSlider = () => {
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
    const subwayLineOptionTemplate =
      subwayLines.map(line => optionTemplate(line)).join("");
    const $stationSelectOptions = document.querySelector(
      "#station-select-options"
    );
    $stationSelectOptions.insertAdjacentHTML(
      "afterbegin",
      subwayLineOptionTemplate
    );
  };

  /* 구간 추가 버튼 */
  const onAddLineStationHandler = async event => {
    event.preventDefault();
    const $lineId = $subwayLineSelection.querySelector("option:checked").getAttribute("data-line-id");

    const preStation = ($subwayDepartStation.value === "") ? null : await api.station.getByName($subwayDepartStation.value);
    const arrivalStation = await api.station.getByName($subwayArrivalStation.value);

    const lineStationData = {
      preStationId: preStation ? preStation.id : null,
      stationId: arrivalStation.id,
      distance: $subwayDistance.value,
      duration: $subwayDuration.value
    };
    api.line.addLineStation($lineId, data).then();
    createSubwayEdgeModal.toggle();
    $subwayLineSelection.value = "";
    $subwayDepartStation.value = "";
    $subwayArrivalStation.value = "";
    $subwayDistance.value = "";
    $subwayDuration.value = "";
    location.reload();
  }

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const $lineId = $target.closest("#line-info").getAttribute("data-line-id");
    const $stationId = $target.closest(".list-item").getAttribute("value");
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      api.line.deleteLineStation($lineId, $stationId).then(() => {
        $target.closest(".list-item").remove();
      });
    }
  };

  const initEventListeners = () => {
    $submitCreateEdgeButton.addEventListener(EVENT_TYPE.CLICK, onAddLineStationHandler);
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
  };

  const initState = async () => {
    subwayLines = await api.line.get();
    console.log(subwayLines);
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
