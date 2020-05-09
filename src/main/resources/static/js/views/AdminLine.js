import { EVENT_TYPE } from "../../utils/constants.js";
import {
	colorSelectOptionTemplate,
	subwayLineInfoTemplate,
	subwayLinesTemplate
} from "../../utils/templates.js";
import { subwayLineColorOptions } from "../../utils/defaultSubwayData.js";
import LineModal from "../../ui/LineModal.js";
import api from "../../api/index.js";

function AdminLine() {
	const $subwayLineList = document.querySelector("#subway-line-list");
	const $subwayLineNameInput = document.querySelector("#subway-line-name");
	const $subwayLineStartTimeInput = document.querySelector("#subway-first-time");
	const $subwayLineEndTimeInput = document.querySelector("#subway-last-time");
	const $subwayLineIntervalTimeInput = document.querySelector("#subway-interval-time");
	const $subwayLineColorInput = document.querySelector("#subway-line-color");
	const $subwayLineFormSubmitButton = document.querySelector(
		"#subway-line-create-form #submit-button");
	const $subwayLinesInfo = document.querySelector(".lines-info");
	let $activeSubwayLineItem = null;

	const subwayLineModal = new LineModal();

	const clearSubwayLineForm = event => {
		$subwayLineNameInput.value = "";
		$subwayLineStartTimeInput.value = "";
		$subwayLineEndTimeInput.value = "";
		$subwayLineIntervalTimeInput.value = "";
		$subwayLineColorInput.value = "";
	}

	const initSubwayLine = () => ({
		title: $subwayLineNameInput.value,
		startTime: $subwayLineStartTimeInput.value,
		endTime: $subwayLineEndTimeInput.value,
		intervalTime: $subwayLineIntervalTimeInput.value,
		bgColor: $subwayLineColorInput.value
	});

	const onCreateSubwayLine = async event => {
		const $target = event.target;
		const isCreateButton = $target.classList.contains("create-btn");
		if (!isCreateButton) {
			return;
		}
		const newSubwayLine = initSubwayLine();
		subwayLineModal.toggle();
		$subwayLineList.insertAdjacentHTML(
			"beforeend",
			subwayLinesTemplate(newSubwayLine)
		);
		await api.line.create(newSubwayLine);
	};

	const onUpdateSubwayLine = async event => {
		const $target = event.target;
		const isUpdateButton = $target.classList.contains("update-btn");
		if (!isUpdateButton) {
			return;
		}
		const updateSubwayLine = initSubwayLine();
		subwayLineModal.toggle();
		await api.line.update(updateSubwayLine, $activeSubwayLineItem.dataset.lineId);
	};

	const onDeleteSubwayLine = async event => {
		const $target = event.target;
		$activeSubwayLineItem = $target.closest(".subway-line-item");
		const isDeleteButton = $target.classList.contains("mdi-delete");
		if (!isDeleteButton) {
			return;
		}
		$target.closest(".subway-line-item").remove();
		await api.line.delete($activeSubwayLineItem.dataset.lineId);
	};

	const onShowUpdateSubwayLineForm = async event => {
		const $target = event.target;
		$activeSubwayLineItem = $target.closest(".subway-line-item");
		const isUpdateButton = $target.classList.contains("mdi-pencil");
		if (!isUpdateButton) {
			return;
		}
		const line = await api.line.get($activeSubwayLineItem.dataset.lineId);
		$subwayLineNameInput.value = line.title;
		$subwayLineStartTimeInput.value = line.startTime.slice(0, -3);
		$subwayLineEndTimeInput.value = line.endTime.slice(0, -3);
		$subwayLineIntervalTimeInput.value = line.intervalTime;
		$subwayLineColorInput.value = line.bgColor;
	};

	const onShowSubwayLineInfo = async event => {
		const $target = event.target;
		const isShowButton = $target.classList.contains("subway-line-item");
		if (isShowButton) {
			const line = await api.line.get($target.dataset.lineId);
			if (line) {
				$subwayLinesInfo.innerHTML = subwayLineInfoTemplate(line);
			}
		}
	};

	const initSubwayLines = async () => {
		const lines = await api.line.get();
		lines.map(line => {
			$subwayLineList.insertAdjacentHTML(
				"beforeend",
				subwayLinesTemplate(line)
			);
		});
	};

	const initEventListeners = () => {
		$subwayLineList.addEventListener(EVENT_TYPE.CLICK, onDeleteSubwayLine);
		$subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowUpdateSubwayLineForm);
		$subwayLineList.addEventListener(EVENT_TYPE.CLICK, onShowSubwayLineInfo);
		$subwayLineFormSubmitButton.addEventListener(EVENT_TYPE.CLICK, onUpdateSubwayLine);
		$subwayLineFormSubmitButton.addEventListener(EVENT_TYPE.CLICK, onCreateSubwayLine);
		subwayLineModal.$closeModalButton.addEventListener(EVENT_TYPE.CLICK, clearSubwayLineForm);
	};

	const onSelectColorHandler = event => {
		event.preventDefault();
		const $target = event.target;
		if ($target.classList.contains("color-select-option")) {
			document.querySelector("#subway-line-color").value =
				$target.dataset.color;
		}
	};

	const initCreateSubwayLineForm = () => {
		const $colorSelectContainer = document.querySelector(
			"#subway-line-color-select-container"
		);
		const colorSelectTemplate = subwayLineColorOptions
			.map((option, index) => colorSelectOptionTemplate(option, index))
			.join("");
		$colorSelectContainer.insertAdjacentHTML("beforeend", colorSelectTemplate);
		$colorSelectContainer.addEventListener(
			EVENT_TYPE.CLICK,
			onSelectColorHandler
		);
	};

	this.init = () => {
		initSubwayLines().then();
		initEventListeners();
		initCreateSubwayLineForm();
	};
}

const adminLine = new AdminLine();
adminLine.init();