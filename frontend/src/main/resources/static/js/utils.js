export function formatDateToDDMMYYYY(dateString) {
if (!dateString) {
        return null; // Si no se introduce una fecha, devolver null
    }

    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
        return null; // Si la fecha no es válida, devolver null
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
}