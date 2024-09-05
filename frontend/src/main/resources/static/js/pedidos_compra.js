const PedidoCompraApp = {
    goBack: () => {
        window.history.back();
    },

    logout: () => {
        window.location.href = "/logout";
    },

    marcarModificado: (elemento) => {
        const fila = elemento.closest('tr');
        fila.classList.add('modificado');
    },

    guardarCambios: () => {
        const filasModificadas = document.querySelectorAll('tbody tr.modificado');

        filasModificadas.forEach(fila => {
            const idPedidoCompra = fila.getAttribute('data-id-pedido-compra');
            const datos = {
                n_operacion: fila.children[2].innerText.trim(),
                n_contenedor: fila.children[3].innerText.trim(),
                proforma: fila.children[4].innerText.trim(),
                proveedor: fila.children[5].innerText.trim(),
                cliente: fila.children[6].innerText.trim(),
                incoterm: fila.children[7].innerText.trim(),
                referenciaProveedor: fila.children[8].innerText.trim()
            };

            if (!idPedidoCompra) {
                fetch('http://localhost:8702/api/compras/pedidos_compra', {
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
                            text: 'Pedido creado con éxito.',
                            toast: true,
                            position: 'top-end',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        }).then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error al crear el pedido.',
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
                        text: 'Error al crear el pedido.',
                        toast: true,
                        position: 'top-end',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                });
            } else {
                fetch(`http://localhost:8702/api/compras/pedidos_compra/${idPedidoCompra}`, {
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
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                        fila.classList.remove('modificado');
                        fila.classList.remove('new-row');
                    } else {
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
        });
    },

    eliminarPedido: (idPedidoCompra) => {
        Swal.fire({
            title: '¿Estás seguro?',
            text: "¿Estás seguro de que deseas eliminar este pedido? ¡Esta acción no se puede deshacer!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                fetch(`http://localhost:8702/api/compras/pedidos_compra/${idPedidoCompra}`, {
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
                            text: 'Pedido eliminado con éxito.',
                            toast: true,
                            position: 'top-end',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        }).then(() => {
                            location.reload(); // Recargar la página después de eliminar
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'Error al eliminar el pedido.',
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
                        text: 'Error al eliminar el pedido.',
                        toast: true,
                        position: 'top-end',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                });
            }
        });
    },

    crearPedidoCompra: () => {
        const tbody = document.querySelector('tbody');
        const nuevaFila = document.createElement('tr');

        nuevaFila.innerHTML = `
            <td>
                <button class="delete-button" onclick="PedidoCompraApp.eliminarFila(this)">🗑️</button>
            </td>
            <td></td>
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
    },

    eliminarFila: (boton) => {
        const fila = boton.closest('tr');
        fila.remove();
    },

    sortTable: (columnIndex) => {
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
    },

    toggleSearch: () => {
        const searchInput = document.getElementById('search-input');
        if (searchInput.style.display === 'none' || searchInput.style.display === '') {
            searchInput.style.display = 'block';
            searchInput.classList.add('expanded');
            searchInput.focus();
        } else {
            searchInput.style.display = 'none';
            searchInput.classList.remove('expanded');
            searchInput.value = '';
            PedidoCompraApp.filtrarPedidos();
        }
    },

    toggleFilter: () => {
        const filterContainer = document.querySelector('.filter-container');
        filterContainer.classList.toggle('expanded');
    },

    filtrarPedidos: () => {
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
    },

    initTableScroll: () => {
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
            const walk = (x - startX) * 2;
            tableContainer.scrollLeft = scrollLeft - walk;
        });
    }
};

// Inicializar la tabla con scroll
document.addEventListener('DOMContentLoaded', PedidoCompraApp.initTableScroll);

// Exponer globalmente
window.PedidoCompraApp = PedidoCompraApp;
