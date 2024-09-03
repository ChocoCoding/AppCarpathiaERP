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
            const idPedidoCompraDet = fila.getAttribute('data-id-pedido-compra-det');
            const idPedidoCompra = fila.children[1].innerText.trim();

            validarExistenciaPedidoCompra(idPedidoCompra)
                        .then(existe => {
                            if (!existe) {
                                alert('El ID del Pedido de Compra no existe. Por favor, verifíquelo.');
                                return;
                            }

        const datos = {
            idPedidoCompra: fila.children[1].innerText.trim(),
            n_operacion: fila.children[2].innerText.trim(),
            contratoCompra: fila.children[3].innerText.trim(),
            terminado: fila.children[4].innerText.trim(),
            factProveedor: fila.children[5].innerText.trim(),
            n_fact_flete: fila.children[6].innerText.trim(),
            fecha_pago_flete: fila.children[7].innerText.trim(),
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
                    alert('Detalle creado con éxito');
                    location.reload();
                } else {
                    alert('Error al crear el detalle');
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                alert('Error al crear el detalle');
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
                    alert('Cambios guardados con éxito');
                    fila.classList.remove('modificado');
                } else {
                    alert('Error al guardar los cambios');
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                alert('Error al guardar los cambios');
            });
        }
    })
            .catch(error => {
                console.error('Error al validar el ID del Pedido de Compra:', error);
                alert('Error al validar el ID del Pedido de Compra.');
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
        alert("No se puede eliminar este detalle porque el ID es nulo.");
        return;
    }

    if (confirm("¿Estás seguro de que deseas eliminar este detalle?")) {
        fetch(`http://localhost:8702/api/compras/pedidos_compra_det/${idPedidoCompraDet}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                alert('Detalle eliminado con éxito');
                location.reload();
            } else {
                alert('Error al eliminar el detalle');
            }
        })
        .catch(error => {
            console.error('Error en la solicitud:', error);
            alert('Error al eliminar el detalle');
        });
    }
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
