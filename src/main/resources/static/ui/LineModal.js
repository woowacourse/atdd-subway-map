import { EVENT_TYPE } from "../utils/constants.js";

export default function LineModal() {
	const $subwayLineAddButton = document.querySelector("#subway-line-add-btn");
	const $closeModalButton = document.querySelector(".modal-close");
	const $body = document.querySelector("body");
	const $modal = document.querySelector(".modal");
	const $subwayLineFormSubmitButton = document.querySelector("#submit-button");
	const $subwayLineList = document.querySelector("#subway-line-list");

	const toggle = event => {
		if (event) {
			event.preventDefault();
		}
		$body.classList.toggle("modal-active");
		$modal.classList.toggle("opacity-0");
		$modal.classList.toggle("pointer-events-none");
		const hasCreatButton = $subwayLineFormSubmitButton.classList.contains("create-btn");
		if (hasCreatButton) {
			$subwayLineFormSubmitButton.classList.remove("create-btn");
		}
		const hasUpdateButton = $subwayLineFormSubmitButton.classList.contains("update-btn");
		if (hasUpdateButton) {
			$subwayLineFormSubmitButton.classList.remove("update-btn");
		}
	};

	const toggleCreateButton = event => {
		toggle(event);
		$subwayLineFormSubmitButton.classList.toggle("create-btn");
	}

	const toggleUpdateButton = event => {
		const $target = event.target;
		const isEditButton = $target.classList.contains("mdi-pencil");
		if (isEditButton) {
			toggle(event);
			$subwayLineFormSubmitButton.classList.toggle("update-btn");
		}
	}

	$subwayLineAddButton.addEventListener(EVENT_TYPE.CLICK, toggleCreateButton);
	$subwayLineList.addEventListener(EVENT_TYPE.CLICK, toggleUpdateButton);
	$closeModalButton.addEventListener(EVENT_TYPE.CLICK, toggle);

	return {
		toggle,
		$closeModalButton,
	};
}
