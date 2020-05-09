import { ERROR_MESSAGE } from './constants.js';

export const validateSubwayName = (name, stations) => {
  if (!name) {
    throw new Error(ERROR_MESSAGE.NOT_EMPTY);
  }
  for (let char of name) {
    if (char === " ") {
      throw new Error(ERROR_MESSAGE.CONTAINS_SPACE);
    }
    if (!isNaN(char)) {
      throw new Error(ERROR_MESSAGE.CONTAINS_NUMBER);
    }
  }
  if (stations.includes(name)) {
    throw new Error(ERROR_MESSAGE.ALREADY_EXISTING);
  }
};
