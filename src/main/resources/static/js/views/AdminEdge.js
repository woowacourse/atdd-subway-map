import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js";

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $createLineStationButton = document.querySelector("#submit-button");
    const $stationSelectOptions = document.querySelector("#station-select-options");
    const createSubwayEdgeModal = new Modal();

    const initSubwayLinesSlider = () => {
        api.line.get().then(data => {
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
        api.line.get().then(data => {
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
        const stations = await api.station.get();
        const preStation = stations.find(station => station.name === document.querySelector("#depart-station-name").value);
        const station = stations.find(station => station.name === document.querySelector("#arrival-station-name").value);
        const request = {
          preStationId: preStation ? preStation.id : null,
          stationId: station ? station.id : null,
          distance: 2,
          duration: 2
        }
        console.log(request);
        api.lineStation.create($stationSelectOptions.options[$stationSelectOptions.selectedIndex].dataset.id, request).then(data => {
          console.log(data);
        })
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
