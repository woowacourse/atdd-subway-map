const dialog = {
  methods: {
    closeDialog() {
      this.close = !this.close;
    },
    isValid($form) {
      return $form.validate();
    }
  },
  data() {
    return {
      close: false,
      valid: false,
      isRequested: false
    };
  }
};

export default dialog;
