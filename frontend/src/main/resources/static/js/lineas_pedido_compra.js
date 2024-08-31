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
        console.log(idLineaPedido)
        const datos = {
            idPedidoCompra: fila.children[1].innerText.trim(),
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
            valor_compra: fila.children[13].innerText.trim(),
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
                    alert('Línea creada con éxito');
                    location.reload();
                } else {
                    alert('Error al crear la línea');
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                alert('Error al crear la línea');
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
    });
}

function eliminarLineaPedido(idLineaPedido) {
console.log("ID de línea de pedido:", idLineaPedido);
    if (!idLineaPedido) {
        console.error("ID de línea de pedido no válido:", idLineaPedido);
        alert("No se puede eliminar esta línea porque el ID es nulo.");
        return;
    }

    console.log('Eliminar línea con ID:', idLineaPedido);
    if (confirm("¿Estás seguro de que deseas eliminar esta línea?")) {
        fetch(`http://localhost:8702/api/compras/lineas_pedidos_compra/${idLineaPedido}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                alert('Línea eliminada con éxito');
                location.reload();
            } else {
                alert('Error al eliminar la línea');
            }
        })
        .catch(error => {
            console.error('Error en la solicitud:', error);
            alert('Error al eliminar la línea');
        });
    }
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