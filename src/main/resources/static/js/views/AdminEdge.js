import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
  const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
  const createSubwayEdgeModal = new Modal();

  const initSubwayLinesSlider = () => {
    $subwayLinesSlider.innerHTML = defaultSubwayLines
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
        api.line.get()
            .then(data => data.json())
            .then(lines => {
                const subwayLineOptionTemplate = lines
                    .map(line => optionTemplate(line))
                    .join("");
                const $stationSelectOptions = document.querySelector(
                    "#station-select-options"
                );
                $stationSelectOptions.insertAdjacentHTML(
                    "afterbegin",
                    subwayLineOptionTemplate
                );
            })
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        const isYes = confirm("정말 삭제하시겠습니까?")
        if (isDeleteButton && isYes) {
            const $deleteStationItem = $target.closest(".list-item");
            const stationId = $deleteStationItem.dataset.stationId;
            const lineId = $target.closest("#subway-edge-list").dataset.lineId;
            api.line.deleteStationById(lineId, stationId)
                .then(() => $deleteStationItem.remove())
                .catch(error => console.log(error));
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $submitEdgeBtn.addEventListener(EVENT_TYPE.CLICK, onAddLineStationHandler);
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
