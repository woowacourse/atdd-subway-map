import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $lineSelectOptions = document.querySelector('#line-select-options');
  const $subwayEdgeAddButton = document.querySelector('#subway-edge-add-btn');
  const $departStationOptions = document.querySelector('#depart-station-options');
  const $arrivalStationOptions = document.querySelector('#arrival-station-options');
  const $createSubwayEdgeButton = document.querySelector("#submit-button");
  const createSubwayEdgeModal = new Modal();
  let subwayLines = [];

  const onCreateSubwayEdge = event => {
    event.preventDefault();

    const lineId = $lineSelectOptions.options[$lineSelectOptions.selectedIndex].dataset.id;
    const preStationId = $departStationOptions.options[$departStationOptions.selectedIndex].dataset.id;
    const stationId = $arrivalStationOptions.options[$arrivalStationOptions.selectedIndex].dataset.id;
    const lineStationCreateRequest = {
      preStationId: preStationId,
      stationId: stationId,
      distance: 10,
      duration: 10
    };

    api.line.createLineStation(lineStationCreateRequest, lineId)
        .then(() => initSubwayLinesView());
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const sliderList = $target.closest(".slider-list");
      const lineId = sliderList.querySelector(".line-name").dataset.id;

      const listItemToDelete = $target.closest(".list-item");
      const stationId = listItemToDelete.querySelector(".station-id").textContent;

      api.line.deleteLineStation(lineId, stationId)
          .then(() => initSubwayLinesView());

      listItemToDelete.remove();
    }
  };

  const initCreateEdgeForm = async event => {
    await event.preventDefault()
    await initLineOptions(subwayLines)
    await initPreviousStationOptions()
    await initNextStationOptions()

    await $createSubwayEdgeButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayEdge);
  };

  const initLineOptions = subwayLines => {
    const subwayLineOptionTemplate = subwayLines
        .map(line => optionTemplate(line))
        .join("");
    $lineSelectOptions.innerHTML = subwayLineOptionTemplate;
  };

  const initPreviousStationOptions = () => {
    api.line.getDetail($lineSelectOptions.options[$lineSelectOptions.selectedIndex].dataset.id).then(line => {
      const stations = line.stations ? line.stations : []
      if (stations.length > 0) {
        $departStationOptions.innerHTML = stations.map(station => optionTemplate(station)).join('')
      }
    })
  };

  const initNextStationOptions = () => {
    api.station
        .get()
        .then(stations => {
          $arrivalStationOptions.innerHTML = stations.map(station => optionTemplate(station)).join('')
        })
        .catch(() => alert(ERROR_MESSAGE.COMMON))
  };

  const initSubwayLinesView = () => {
    api.line.get()
        .then(data => {
          subwayLines = data;
          $subwayLinesSlider.innerHTML = data.map(line => subwayLinesItemTemplate(line))
              .join("");

        })
        .then(() => initSubwayLinesSlider());
  };

  const initSubwayLinesSlider = () => {
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

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(EVENT_TYPE.CLICK, onRemoveStationHandler);
    $subwayEdgeAddButton.addEventListener(EVENT_TYPE.CLICK, initCreateEdgeForm);
  };

  this.init = async () => {
    await initSubwayLinesView();
    await initLineOptions(subwayLines);
    await initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();