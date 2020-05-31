import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const $createLineStationButton = document.querySelector("#submit-button");
  const $stationSelectOptions = document.querySelector("#station-select-options");
  const $departStationName = document.querySelector("#depart-station-name");
  const $arrivalStationName = document.querySelector("#arrival-station-name");
  const $distanceField = document.querySelector("#distance");
  const $durationField = document.querySelector("#duration");

  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
    api.line.getAll().then(data => {
      $subwayLinesSlider.innerHTML = data.map(line => subwayLinesItemTemplate(line))
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
    });
  };


  const initSubwayLineOptions = () => {
    api.line.getAll().then(data => {
      const subwayLineOptionTemplate = data.map(line => optionTemplate(line))
          .join("")
      const $stationSelectOptions = document.querySelector(
          "#station-select-options"
      );
      $stationSelectOptions.insertAdjacentHTML(
          "afterbegin",
          subwayLineOptionTemplate
      );
    });
  };

  const onCreateLineStationHandler = async event => {
    event.preventDefault();
    const stations = await api.station.getAll();
    const preStationData = stations.find(station => station.name === $departStationName.value);
    const stationData = stations.find(station => station.name === $arrivalStationName.value);
    const request = {
      preStation: preStationData ? preStationData.id : 0,
      station: stationData ? stationData.id : 0,
      distance: $distanceField.value,
      duration: $durationField.value
    }
    console.log($stationSelectOptions.options[$stationSelectOptions.selectedIndex].dataset);
    api.line.addLineStation($stationSelectOptions.options[$stationSelectOptions.selectedIndex].dataset.id, request).then(data => {
      if (data.error) throw data;
      createSubwayEdgeModal.toggle();
      location.reload();
    }).catch(e => {
      alert("올바르지 않은 요청입니다.");
    })
  };

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const lineId = $target.closest(".slider-list").dataset.id;
      const stationId = $target.closest(".list-item").dataset.id;
      api.line.deleteLineStation(lineId, stationId).then(() => {
        $target.closest(".list-item").remove();
      })
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
        EVENT_TYPE.CLICK,
        onRemoveStationHandler
    );
    $createLineStationButton.addEventListener(
        EVENT_TYPE.CLICK,
        onCreateLineStationHandler
    );
  };

  this.init = () => {
    initSubwayLinesSlider();
    initSubwayLineOptions();
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
