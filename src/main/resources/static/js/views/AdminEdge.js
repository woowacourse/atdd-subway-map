import {optionTemplate, subwayLinesItemTemplate} from "../../utils/templates.js";
import tns from "../../lib/slider/tiny-slider.js";
import {EVENT_TYPE} from "../../utils/constants.js";
import Modal from "../../ui/Modal.js";
import api from "../../api/index.js"
import {stationOptionTemplate} from "../../utils/templates.js";

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
        ).catch(() => alert("노선 조회 중 에러가 발생했습니다."));


    };

    const initSubwayLineOptions = () => {
        api.line.get().then(subwayLines => {
            const subwayLineOptionTemplate = subwayLines
                .map(line => {
                    return optionTemplate(line)
                })
                .join("");

            const $stationSelectOptions = document.querySelector(
                "#station-select-options"
            );
            $stationSelectOptions.insertAdjacentHTML(
                "afterbegin",
                subwayLineOptionTemplate
            );
        }).catch(() => "노선 조회 중 에러가 발생했습니다.");
    };

    const initSubwayStationOptions = () => {
        api.station
            .get()
            .then(stations => {
                const stationsOptionTemplate = stations
                    .map(station => stationOptionTemplate(station))
                .join("");

            const $stationSelectOptions = document.querySelectorAll(
                ".station-select-options"
            );

            $stationSelectOptions.forEach(select => {
                    select.insertAdjacentHTML(
                    "beforeend",
                    stationsOptionTemplate
                );
            });

        })

    }



    const onSubwayLineAddBtnClicked = event => {
        $submitButton.classList.add('subway-line-add-button');
    };

    const onSubmitClicked = async event => {
        const isSubwayLineAddButton = event.target.classList.contains("subway-line-add-button");
        if (!isSubwayLineAddButton) {
            return;
        }
        const $selectOptions = document.querySelector("#station-select-options");
        const $selectedOption = $selectOptions[$selectOptions.selectedIndex];
        const lineId = $selectedOption.dataset.lineId;
        const $preStationOptions = document.querySelector("#pre-station-name");
        const $stationOptions = document.querySelector("#station-name");
        const $selectedPreStationOption = $preStationOptions[$preStationOptions.selectedIndex];
        const $selectedStationOption = $stationOptions[$stationOptions.selectedIndex];
        const preStationId = $selectedPreStationOption.dataset.stationId;
        const stationId = $selectedStationOption.dataset.stationId;

        const lineStationDto = {
            id: lineId,
            preStationId: preStationId,
            stationId: stationId,
            distance: "10",
            duration: "10"
        };
        try {
          const response = await api.line
            .registerLineStation(lineStationDto)
          createSubwayEdgeModal.toggle()
        } catch (e) {
          alert("구간 등록 중 에러가 발생했습니다.")
        }
    };

    const onRemoveStationHandler = event => {
        const $target = event.target;
        const isDeleteButton = $target.classList.contains("mdi-delete");
        if (isDeleteButton) {

            const $id = $target.dataset.stationId;
            $target.closest(".list-item").remove();
            api.station.delete($id)
                .then()
                .catch(() => alert("역 삭제 중 에러가 발생했습니다."));
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
        initSubwayStationOptions();
    };
}

const adminEdge = new AdminEdge();
adminEdge.init();
