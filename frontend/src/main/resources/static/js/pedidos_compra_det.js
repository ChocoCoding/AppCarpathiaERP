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

function mostrarAlerta(tipo, titulo, texto, timer = 2000) {
    Swal.fire({
        icon: tipo,
        title: titulo,
        text: texto,
        toast: true,
        position: 'top-end',
        timer: timer,
        timerProgressBar: true,
        showConfirmButton: false
    });
}

function guardarCambios() {
    const filasModificadas = document.querySelectorAll('tbody tr.modificado');
    const formatoFecha = /^\d{2}\/\d{2}\/\d{4}$/;

    filasModificadas.forEach(fila => {
        const idPedidoCompraDet = fila.getAttribute('data-id-pedido-compra-det');
        const idPedidoCompra = fila.children[1].innerText.trim();
        const fechaPagoFlete = fila.children[7].innerText.trim(); // Suponiendo que la fecha está en la columna 7
        const terminado = fila.children[4].innerText.trim().toUpperCase(); // Suponiendo que el campo terminado está en la columna 4

        // Validar el formato de la fecha
        if (fechaPagoFlete && !formatoFecha.test(fechaPagoFlete)) {
            mostrarAlerta('error', 'Formato de fecha incorrecto', 'El formato de fecha es incorrecto. Debe ser dd/MM/yyyy.', 3000);
            return;
        }

        // Validar que el campo terminado sea 'S' o 'N'
        if (terminado && terminado !== 'S' && terminado !== 'N') {
            mostrarAlerta('error', 'Valor incorrecto', 'El campo "Terminado" debe ser "S" o "N".', 3000);
            return;
        }

        validarExistenciaPedidoCompra(idPedidoCompra)
            .then(existe => {
                if (!existe) {
                    mostrarAlerta('error', 'ID no válido', 'El ID del Pedido de Compra no existe. Por favor, verifíquelo.', 3000);
                    return;
                }

                const datos = {
                    idPedidoCompra: fila.children[1].innerText.trim(),
                    n_operacion: fila.children[2].innerText.trim(),
                    contratoCompra: fila.children[3].innerText.trim(),
                    terminado: terminado,
                    factProveedor: fila.children[5].innerText.trim(),
                    n_fact_flete: fila.children[6].innerText.trim(),
                    fecha_pago_flete: fechaPagoFlete,
                    n_bl: fila.children[8].innerText.trim(),
                    pesoNetoTotal: fila.children[9].innerText.trim(),
                    totalBultos: fila.children[10].innerText.trim(),
                    promedio: fila.children[11].innerText.trim(),
                    valorCompraTotal: fila.children[12].innerText.trim(),
                    observaciones: fila.children[13].innerText.trim()
                };

                if (!idPedidoCompraDet) {
                    // Crear un nuevo detalle de pedido de compra
                    fetch('http://localhost:8702/api/compras/pedidos_compra_det', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(datos)
                    })
                    .then(response => {
                        if (response.ok) {
                            mostrarAlerta('success', 'Creación exitosa', 'Detalle creado con éxito.', 2000);
                            location.reload();
                        } else {
                            mostrarAlerta('error', 'Error', 'Error al crear el detalle.');
                        }
                    })
                    .catch(error => {
                        console.error('Error en la solicitud:', error);
                        mostrarAlerta('error', 'Error', 'Error al crear el detalle.');
                    });
                } else {
                    // Actualizar el detalle existente
                    fetch(`http://localhost:8702/api/compras/pedidos_compra_det/${idPedidoCompraDet}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(datos)
                    })
                    .then(response => {
                        if (response.ok) {
                            mostrarAlerta('success', 'Guardado exitoso', 'Cambios guardados con éxito.', 2000);
                            fila.classList.remove('modificado');
                        } else {
                            mostrarAlerta('error', 'Error', 'Error al guardar los cambios.');
                        }
                    })
                    .catch(error => {
                        console.error('Error en la solicitud:', error);
                        mostrarAlerta('error', 'Error', 'Error al guardar los cambios.');
                    });
                }
            })
            .catch(error => {
                console.error('Error al validar el ID del Pedido de Compra:', error);
                mostrarAlerta('error', 'Error', 'Error al validar el ID del Pedido de Compra.');
            });
    });
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

function eliminarPedidoCompraDet(idPedidoCompraDet) {
    if (!idPedidoCompraDet) {
        console.error("ID de detalle de pedido de compra no válido:", idPedidoCompraDet);
        mostrarAlerta('error', 'ID no válido', 'No se puede eliminar este detalle porque el ID es nulo.');
        return;
    }

    Swal.fire({
        title: '¿Estás seguro?',
        text: "¿Estás seguro de que deseas eliminar este detalle? ¡Esta acción no se puede deshacer!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`http://localhost:8702/api/compras/pedidos_compra_det/${idPedidoCompraDet}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    mostrarAlerta('success', 'Eliminado', 'Detalle eliminado con éxito.', 2000);
                    location.reload(); // Recargar la página después de eliminar
                } else {
                    mostrarAlerta('error', 'Error', 'Error al eliminar el detalle.');
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                mostrarAlerta('error', 'Error', 'Error al eliminar el detalle.');
            });
        }
    });
}



function crearPedidoCompraDet() {
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
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
        <td contenteditable="true" class="editable"></td>
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

function filtrarDetalles() {
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
