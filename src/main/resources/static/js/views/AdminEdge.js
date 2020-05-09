import { subwayLinesItemTemplate } from "../../utils/templates.js";
import { defaultSubwayLines } from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import { EVENT_TYPE } from "../../utils/constants.js";
import CreateSubWayEdgeModal from "../../ui/CreateSubwayEdgeModal.js"
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const subwayEdgeModal = new CreateSubWayEdgeModal();

  let subwayLines = [];

  const initSubwayLinesSlider = () => {
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

  const onRemoveStationHandler = event => {
    const $target = event.target;
    const isDeleteButton = $target.classList.contains("mdi-delete");
    if (isDeleteButton) {
      $target.closest(".list-item").remove();
    }
  };

  const initEventListeners = () => {
    $subwayLinesSlider.addEventListener(
      EVENT_TYPE.CLICK,
      onRemoveStationHandler
    );
  };

  const initState = async () => {
    subwayLines = await api.edge.get();
  };

  this.init = async () => {
    await initState();
    console.log(subwayLines);
    initSubwayLinesSlider();
    subwayEdgeModal.init(subwayLines);
    initEventListeners();
  };
}

const adminEdge = new AdminEdge();
adminEdge.init();
