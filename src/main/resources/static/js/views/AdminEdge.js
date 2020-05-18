import {
  optionTemplate,
  subwayLinesItemTemplate
} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();
  const $subwayEdgeSummitButton = document.querySelector("#submit-button");
  const $subwayEdgeLineInput = document.querySelector("#station-select-options");
  const $subwayEdgeDepartName = document.querySelector("#depart-station-name");
  const $subwayEdgeArrivalName = document.querySelector("#arrival-station-name");
  const $subwayEdgeDistance = document.querySelector("#station-distance");
  const $subwayEdgeDuration = document.querySelector("#arrival-time");

  const initSubwayLinesSlider = () => {
    api.line.get()
      .then(data => {
        $subwayLinesSlider.innerHTML= data.map(line => subwayLinesItemTemplate(line)).join("");
      })
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
      });
  };

  const initSubwayLineOptions = () => {
    api.line.get()
      .then(data => {
        const subwayLineOptionTemplate = data.map(line => optionTemplate(line));
        const $stationSelectOptions = document.querySelector(
          "#station-select-options"
        );
        $stationSelectOptions.insertAdjacentHTML(
          "afterbegin",
          subwayLineOptionTemplate
        );
      });
  };

  const onCreateStationHandler = event => {
    event.preventDefault();
    const $target = event.target;
    const isSummitButton = $target.id === "submit-button";
    if (!isSummitButton) {
      return;
    }
    api.station.get()
      .then(stations => {
        const preStation = stations.find(station => station.name === $subwayEdgeDepartName.value);
        const station = stations.find(station => station.name === $subwayEdgeArrivalName.value);
        if (!station || (!preStation && $subwayEdgeDepartName.value)) {
          alert("해당되는 역이 존재하지 않습니다.");
          return;
        }
        createStation(preStation ? preStation.id : null, station.id);
      })
  };

  const createStation = (preStationId, stationId) => {
    const lineId = $subwayEdgeLineInput.value;
    const newSubwayLineStationData = {
      preStationId: preStationId,
      stationId: stationId,
      distance: $subwayEdgeDistance.value,
      duration: $subwayEdgeDuration.value
    };
    api.lineStation.create(newSubwayLineStationData, lineId)
      .then(() => {
        $subwayEdgeDepartName.value = "";
        $subwayEdgeArrivalName.value = "";
        $subwayEdgeDistance.value = "";
        $subwayEdgeDuration.value = "";
        initSubwayLinesSlider();
        createSubwayEdgeModal.toggle();
      });
  };

  const onRemoveStationHandler = event => {
    event.preventDefault();
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      const lineId = $target.closest(".tns-item").dataset.lineId;
      const stationId = $target.closest(".list-item").dataset.stationId;
      api.lineStation.delete(lineId, stationId)
        .then(() => initSubwayLinesSlider());
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
    $subwayEdgeSummitButton.addEventListener(
      EVENT_TYPE.CLICK,
      onCreateStationHandler
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
