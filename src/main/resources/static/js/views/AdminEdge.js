import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"

function AdminEdge() {
    const $subwayLinesSlider = document.querySelector(".subway-lines-slider");
    const $subwayLineAddButton = document.querySelector("#subway-line-add-btn");
    const createSubwayEdgeModal = new Modal();
    const $submitButton =  document.querySelector('#submit-button');

    const initSubwayLinesSlider = () => {

        api.line.get().then(subwayLines => {
                $subwayLinesSlider.innerHTML = subwayLines
                    .map(line => {
                            const subwayLine = {title: line.title, bgColor: line.bgColor, stations: line.stations,}
                            return subwayLinesItemTemplate(subwayLine);
                        }
                    )
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
            }
        );


    };

    const initSubwayLineOptions = () => {
        api.line.get().then(subwayLines => {
            const subwayLineOptionTemplate = subwayLines
                .map(line => {
                    return optionTemplate(line.title)
                })
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

    const onSubwayLineAddBtnClicked = event => {
        $submitButton.classList.add('subway-line-add-button');
    };

    const onSubmitClicked = event => {
        const isSubwayLineAddButton = event.target.classList.contains("subway-line-add-button");
        if (!isSubwayLineAddButton) {
            return;
        }
        const $selectOptions = document.querySelector("#station-select-options");

        const lineStationDto = {
            name: $selectOptions[$selectOptions.selectedIndex].value,
            preStationName: document.querySelector("#depart-station-name").value,
            arrivalStationName: document.querySelector("#arrival-station-name").value

        };

        api.line
            .registerLineStation(lineStationDto)
            .then(response => createSubwayEdgeModal.toggle());
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {

            const $id = $target.dataset.stationId;
            $target.closest(".list-item").remove();
            api.station.delete($id).then();
        }
    };

    const initEventListeners = () => {
        $subwayLinesSlider.addEventListener(
            EVENT_TYPE.CLICK,
            onRemoveStationHandler
        );

        $subwayLineAddButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubwayLineAddBtnClicked
        );
        $submitButton.addEventListener(
            EVENT_TYPE.CLICK,
            onSubmitClicked
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
