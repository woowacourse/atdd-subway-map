const EventEmitter = () => {
  const events = new Map();

  const on = (type, listener) => {
      const event = events.get(type);
      if (event) {
      event.push(listener);
      return;
      }
      events.set(type, [listener]);
  };

  const emit = (type, data) => {
      events.get(type).map(listener => listener(data));
  };

  return {
      on,
      emit
  };
}

export default EventEmitter;