import middleware from '/js/middleware.js';

let config = {};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')  // Asegúrate de que la URL sea correcta y accesible
        .then(response => response.json())
        .then(data => {
            config = data;
            return config;
        })
        .catch(error => {
            console.error('Error al cargar la configuración:', error);
        });
}

cargarConfiguraciones().then(() => {
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

        mostrarAlerta: (tipo, titulo, texto, timer = 2000) => {
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
                    middleware.post(config.pedidosCompraEndpoint, datos)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.creacionExitosa, config.pedidoCreadoExito);
                            setTimeout(() => location.reload(), 2000);
                        })
                        .catch(() => {
                            PedidoCompraApp.mostrarAlerta('error', config.error, config.errorCrearPedido);
                        });
                } else {
                    const url = config.pedidoCompraIdEndpoint.replace('{id}', idPedidoCompra);
                    middleware.put(url, datos)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito);
                            fila.classList.remove('modificado');
                            fila.classList.remove('new-row');
                        })
                        .catch(() => {
                            PedidoCompraApp.mostrarAlerta('error', config.error, config.errorGuardarCambios);
                        });
                }
            });
        },

        eliminarPedido: (idPedidoCompra) => {

            Swal.fire({
                title: config.eliminarPedidoConfirmacion,
                text: config.eliminarPedidoConfirmacion,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.pedidoCompraIdEndpoint.replace('{id}', idPedidoCompra);
                    middleware.delete(url)
                        .then(() => {
                            PedidoCompraApp.mostrarAlerta('success', config.pedidoEliminadoExito, config.pedidoEliminadoExito);
                            setTimeout(() => location.reload(), 1000);
                        })
                        .catch(() => {
                            PedidoCompraApp.mostrarAlerta('error', config.error, config.errorEliminarPedido);
                            setTimeout(() => location.reload(), 1000);
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

    // Verificar si la búsqueda es completamente numérica para aplicar comparación exacta
    const esBusquedaExacta = /^\d+$/.test(searchInput);

    filas.forEach(fila => {
        const columnas = fila.querySelectorAll('td');
        let match = false;

        columnas.forEach((columna, index) => {
            const columnaTexto = columna.innerText.toLowerCase().trim();

            if ((columnasSeleccionadas.length === 0 || columnasSeleccionadas.includes(index + 1))) {
                if (esBusquedaExacta) {
                    // Si la búsqueda es numérica, se hace comparación exacta
                    if (columnaTexto === searchInput) {
                        match = true;
                    }
                } else {
                    // Si la búsqueda no es numérica, usar includes para coincidencias parciales
                    if (columnaTexto.includes(searchInput)) {
                        match = true;
                    }
                }
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
    document.addEventListener('DOMContentLoaded', PedidoCompraApp.initTableScroll());

    // Exponer globalmente
    window.PedidoCompraApp = PedidoCompraApp;
});
