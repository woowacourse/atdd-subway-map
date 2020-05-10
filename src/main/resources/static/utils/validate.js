export const markingErrorField = (response) => {
    if (!response.error) {
        return;
    }
    let errors = response.errors;

    for (let i in errors) {
        alert(errors[i].defaultMessage);
    }
}