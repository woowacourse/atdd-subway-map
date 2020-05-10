import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import {defaultSubwayLines} from "../../utils/subwayMockData.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"

function AdminEdge() {
    const $stations = {};

    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $subwayCreateButton = document.querySelector("#submit-button");
    const $line = document.querySelector("#station-select-options");
    const $preStation = document.querySelector("#depart-station-name");
    const $station = document.querySelector("#arrival-station-name");

    const createSubwayEdgeModal = new Modal();

    api.station.get().then(stations => {
        stations.body.map(station => $stations[station.name] = station.id);
    });

    const initSubwayLinesSlider = () => {
        api.line.get().then((subwayLines) => {
            $subwayLinesSlider.innerHTML = subwayLines.body.map(line => subwayLinesItemTemplate(line))
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
        api.line.get().then(subwayLines => {
            const subwayLineOptionTemplate = subwayLines.body
                .map(line => optionTemplate(line))
                .join("");
            const $stationSelectOptions = document.querySelector(
                "#station-select-options"
            );
            $stationSelectOptions.insertAdjacentHTML(
                "afterbegin",
                subwayLineOptionTemplate
            );
        });
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {
            $target.closest(".list-item").remove();
        }
    };

    const onCreateStationHandler = event => {
        event.preventDefault();
        const edgeCreateRequest = {
            preStationId: $stations[$preStation.value],
            stationId: $stations[$station.value],
        };

        api.edge.create(edgeCreateRequest, $line.value)
            .then(() => {
                alert(`추가되었습니다.`);
                createSubwayEdgeModal.toggle();
            })
            .catch(error => {
                console.error(error);
            });
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );
        $subwayCreateButton.addEventListener(
            EVENT_TYPE.CLICK,
            onCreateStationHandler
        )
    };

    this.init = () => {
        initSubwayLinesSlider();
        initSubwayLineOptions();
        initEventListeners();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
