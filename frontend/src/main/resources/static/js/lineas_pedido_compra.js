function goBack() {
    window.history.back();
}

function logout() {
    window.location.href = "/logout";
}

function marcarModificado(elemento) {
    const fila = elemento.closest('tr');
    fila.classList.add('modificado');
}

function guardarCambios() {
    const filasModificadas = document.querySelectorAll('tbody tr.modificado');

    filasModificadas.forEach(fila => {
        const idLineaPedido = fila.getAttribute('data-id-linea-pedido');
        const idPedidoCompra = fila.children[1].innerText.trim();

        // Validar si el idPedidoCompra existe antes de continuar
        validarExistenciaPedidoCompra(idPedidoCompra)
            .then(existe => {
                if (!existe) {
                    Swal.fire({
                        icon: 'error',
                        title: 'ID no válido',
                        text: 'El ID del Pedido de Compra no existe. Por favor, verifíquelo.',
                        toast: true,
                        position: 'top-end',
                        timer: 3000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                    return;
                }

                // Validaciones de formato para campos específicos
                const precioField = fila.querySelector('.precio');
                const pesoNetoField = fila.querySelector('.peso-neto');

                const datos = {
                    idPedidoCompra: idPedidoCompra,
                    n_linea: fila.children[2].innerText.trim(),
                    n_operacion: fila.children[3].innerText.trim(),
                    proveedor: fila.children[4].innerText.trim(),
                    cliente: fila.children[5].innerText.trim(),
                    n_contenedor: fila.children[6].innerText.trim(),
                    producto: fila.children[7].innerText.trim(),
                    talla: fila.children[8].innerText.trim(),
                    p_neto: fila.children[9].innerText.trim(),
                    unidad: fila.children[10].innerText.trim(),
                    bultos: fila.children[11].innerText.trim(),
                    precio: fila.children[12].innerText.trim(),
                    valor_compra: fila.querySelector('.valor-compra-total').innerText.trim(),
                    moneda: fila.children[14].innerText.trim(),
                    paisOrigen: fila.children[15].innerText.trim()
                };

                if (!idLineaPedido) {
                    // Crear una nueva línea de pedido
                    fetch('http://localhost:8702/api/compras/lineas_pedidos_compra', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(datos)
                    })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Creación exitosa',
                                text: 'Línea creada con éxito.',
                                toast: true,
                                position: 'top-end',
                                timer: 500,
                                timerProgressBar: true,
                                showConfirmButton: false
                            }).then(() => {
                                location.reload();
                            });
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'Error al crear la línea.',
                                toast: true,
                                position: 'top-end',
                                timer: 2000,
                                timerProgressBar: true,
                                showConfirmButton: false
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error en la solicitud:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error al crear la línea.',
                            toast: true,
                            position: 'top-end',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                    });
                } else {
                    // Actualizar la línea existente
                    fetch(`http://localhost:8702/api/compras/lineas_pedidos_compra/${idLineaPedido}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(datos)
                    })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire({
                                icon: 'success',
                                title: 'Guardado exitoso',
                                text: 'Cambios guardados con éxito.',
                                toast: true,
                                position: 'top-end',
                                timer: 500,
                                timerProgressBar: true,
                                showConfirmButton: false
                            });
                            fila.classList.remove('modificado');
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'Error al guardar los cambios. Revisa los campos',
                                toast: true,
                                position: 'top-end',
                                timer: 2000,
                                timerProgressBar: true,
                                showConfirmButton: false
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error en la solicitud:', error);
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error al guardar los cambios.',
                            toast: true,
                            position: 'top-end',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                    });
                }
            })
            .catch(error => {
                console.error('Error al validar el ID del Pedido de Compra:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Error al validar el ID del Pedido de Compra.',
                    toast: true,
                    position: 'top-end',
                    timer: 2000,
                    timerProgressBar: true,
                    showConfirmButton: false
                });
            });
    });
}

function calcularValorCompra(elemento) {
    const fila = elemento.closest('tr');
    const pesoNetoField = fila.querySelector('.peso-neto');
    const precioField = fila.querySelector('.precio');

    // Función para corregir el formato y mantener la posición del cursor
    function corregirFormatoDecimal(field) {
        let selection = window.getSelection();
        let range = selection.getRangeAt(0);
        let startOffset = range.startOffset;

        // Reemplazar comas por puntos
        const originalText = field.innerText;
        const newText = originalText.replace(',', '.');

        // Actualiza el texto solo si es necesario
        if (originalText !== newText) {
            field.innerText = newText;

            // Coloca el cursor en la posición correcta
            let textNode = field.firstChild;
            if (textNode) {
                let newOffset = Math.min(startOffset + (newText.length - originalText.length), newText.length);
                range.setStart(textNode, newOffset);
                range.setEnd(textNode, newOffset);
                selection.removeAllRanges();
                selection.addRange(range);
            }
        }
    }

    // Aplicar corrección de formato en ambos campos
    corregirFormatoDecimal(pesoNetoField);
    corregirFormatoDecimal(precioField);

    // Convertir el valor a número flotante con precisión completa
    const pesoNeto = parseFloat(pesoNetoField.innerText.trim()) || 0;
    const precio = parseFloat(precioField.innerText.trim()) || 0;

    // Multiplicar considerando la precisión completa
    const valorCompraTotal = (pesoNeto * precio).toFixed(6); // Ajustar a 6 decimales, si es necesario

    // Mostrar el resultado con la precisión deseada (por ejemplo, 6 decimales)
    fila.querySelector('.valor-compra-total').innerText = valorCompraTotal;

    fila.classList.add('modificado'); // Marca la fila como modificada
}




function validarExistenciaPedidoCompra(idPedidoCompra) {
    return fetch(`http://localhost:8702/api/compras/pedidos_compra/${idPedidoCompra}/exists`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => data.existe)
    .catch(error => {
        console.error('Error en la validación del ID del Pedido de Compra:', error);
        return false;
    });
}

function eliminarLineaPedido(idLineaPedido) {
    if (!idLineaPedido) {
        console.error("ID de línea de pedido no válido:", idLineaPedido);
        Swal.fire({
            icon: 'error',
            title: 'ID no válido',
            text: 'No se puede eliminar esta línea porque el ID es nulo.',
            toast: true,
            position: 'top-end',
            timer: 2000,
            timerProgressBar: true,
            showConfirmButton: false
        });
        return;
    }

    Swal.fire({
        title: '¿Estás seguro?',
        text: "¿Estás seguro de que deseas eliminar esta línea? ¡Esta acción no se puede deshacer!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`http://localhost:8702/api/compras/lineas_pedidos_compra/${idLineaPedido}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Eliminado',
                        text: 'Línea eliminada con éxito.',
                        toast: true,
                        position: 'top-end',
                        timer: 300,
                        timerProgressBar: true,
                        showConfirmButton: false
                    }).then(() => {
                        location.reload(); // Recargar la página después de eliminar
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al eliminar la línea.',
                        toast: true,
                        position: 'top-end',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Error al eliminar la línea.',
                    toast: true,
                    position: 'top-end',
                    timer: 2000,
                    timerProgressBar: true,
                    showConfirmButton: false
                });
            });
        }
    });
}

function crearLineaPedido() {
    const tbody = document.querySelector('tbody');
    const nuevaFila = document.createElement('tr');

    nuevaFila.innerHTML = `
        <td>
            <button class="delete-button" onclick="eliminarFila(this)">🗑️</button>
        </td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable peso-neto" oninput="calcularValorCompra(this)"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable precio" oninput="calcularValorCompra(this)"></td>
        <td contenteditable="true" class="editable valor-compra-total"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
    `;

    tbody.insertBefore(nuevaFila, tbody.firstChild);
    nuevaFila.classList.add('modificado', 'new-row');
}

function eliminarFila(boton) {
    const fila = boton.closest('tr');
    fila.remove();
}

function sortTable(columnIndex) {
    const table = document.querySelector("tbody");
    const rows = Array.from(table.querySelectorAll("tr"));
    const isAscending = table.getAttribute("data-sort-direction") === "asc";
    const newDirection = isAscending ? "desc" : "asc";

    rows.sort((a, b) => {
        const aText = a.children[columnIndex].innerText.trim();
        const bText = b.children[columnIndex].innerText.trim();

        return isAscending
            ? aText.localeCompare(bText, undefined, {numeric: true})
            : bText.localeCompare(aText, undefined, {numeric: true});
    });

    rows.forEach(row => table.appendChild(row));

    table.setAttribute("data-sort-direction", newDirection);

    document.querySelectorAll(".sortable").forEach(th => th.classList.remove("asc", "desc"));
    document.querySelector(`.sortable:nth-child(${columnIndex + 1})`).classList.add(newDirection);
}

function toggleSearch() {
    const searchInput = document.getElementById('search-input');
    if (searchInput.style.display === 'none' || searchInput.style.display === '') {
        searchInput.style.display = 'block';
        searchInput.classList.add('expanded');
        searchInput.focus();
    } else {
        searchInput.style.display = 'none';
        searchInput.classList.remove('expanded');
        searchInput.value = '';
        filtrarLineas();
    }
}

function toggleFilter() {
    const filterContainer = document.querySelector('.filter-container');
    filterContainer.classList.toggle('expanded');
}

function filtrarLineas() {
    const searchInput = document.getElementById('search-input').value.toLowerCase();
    const columnasSeleccionadas = Array.from(document.querySelectorAll('input[name="columnFilter"]:checked')).map(input => parseInt(input.value));
    const filas = document.querySelectorAll('tbody tr');

    filas.forEach(fila => {
        const columnas = fila.querySelectorAll('td');
        let match = false;

        columnas.forEach((columna, index) => {
            if ((columnasSeleccionadas.length === 0 || columnasSeleccionadas.includes(index + 1)) &&
                columna.innerText.toLowerCase().includes(searchInput) && index !== 0) {
                match = true;
            }
        });

        if (match) {
            fila.style.display = '';
        } else {
            fila.style.display = 'none';
        }
    });
}

// Ocultar el campo de búsqueda y los filtros cuando se hace clic fuera de ellos
document.addEventListener('click', function(event) {
    const searchContainer = document.querySelector('.search-container');
    const searchInput = document.getElementById('search-input');
    const filterContainer = document.querySelector('.filter-container');

    if (!searchContainer.contains(event.target)) {
        searchInput.style.display = 'none';
        searchInput.classList.remove('expanded');
        filterContainer.classList.remove('expanded');
   }
});

document.addEventListener('DOMContentLoaded', function () {
    const tableContainer = document.querySelector('.table-container');
    let isDown = false;
    let startX;
    let scrollLeft;

    tableContainer.addEventListener('mousedown', (e) => {
        isDown = true;
        tableContainer.classList.add('active');
        startX = e.pageX - tableContainer.offsetLeft;
        scrollLeft = tableContainer.scrollLeft;
    });

    tableContainer.addEventListener('mouseleave', () => {
        isDown = false;
        tableContainer.classList.remove('active');
    });

    tableContainer.addEventListener('mouseup', () => {
        isDown = false;
        tableContainer.classList.remove('active');
    });

    tableContainer.addEventListener('mousemove', (e) => {
        if (!isDown) return;
        e.preventDefault();
        const x = e.pageX - tableContainer.offsetLeft;
        const walk = (x - startX) * 2; // El valor multiplica la velocidad de desplazamiento
        tableContainer.scrollLeft = scrollLeft - walk;
    });
});

