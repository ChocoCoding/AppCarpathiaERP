import middleware from '/js/middleware.js';

let config = {};

// Cargar configuraciones de endpoints y mensajes desde el backend
function cargarConfiguraciones() {
    return fetch('http://localhost:8702/api/config')
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
    const CosteCompraApp = {
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
                const idCosteCompra = fila.getAttribute('data-id-coste-compra');
                const idPedidoCompra = fila.children[1].innerText.trim();

                CosteCompraApp.validarExistenciaPedidoCompra(idPedidoCompra)
                    .then(existe => {
                        if (!existe) {
                            CosteCompraApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido, 3000);
                            return;
                        }

                        const datos = {
                            idPedidoCompra,
                            n_operacion: fila.children[2].innerText.trim(),
                            n_contenedor: fila.children[3].innerText.trim(),
                            arancel:fila.children[4].innerText.trim(),
                            sanidad: fila.children[5].innerText.trim(),
                            plastico: fila.children[6].innerText.trim(),
                            carga: fila.children[7].innerText.trim(),
                            inland: fila.children[8].innerText.trim(),
                            muellaje: fila.children[9].innerText.trim(),
                            pif: fila.children[10].innerText.trim(),
                            despacho: fila.children[11].innerText.trim(),
                            conexiones: fila.children[12].innerText.trim(),
                            iva: fila.children[13].innerText.trim(),
                            dec_iva: fila.children[14].innerText.trim(),
                            tasa_sanitaria: fila.children[15].innerText.trim(),
                            suma_costes: fila.children[16].innerText.trim(),
                            gasto_total: fila.children[17].innerText.trim()
                        };

                        if (!idCosteCompra) {
                            middleware.post(config.costesPedidosCompraEndpoint, datos)
                                .then(() => {
                                    CosteCompraApp.mostrarAlerta('success', config.creacionExitosa, config.costeCreadoExito, 2000);
                                    location.reload();
                                })
                                .catch(error => {
                                    if(error.message.includes('El coste ya existe.')){
                                         CosteCompraApp.mostrarAlerta('error',config.error, config.costeYaExiste);
                                         }else{
                                            CosteCompraApp.mostrarAlerta('error', config.error, config.errorCrearCoste);
                                         }
                                });
                        } else {
                            const url = config.costesPedidosCompraIdEndpoint.replace('{id}', idCosteCompra);
                            middleware.put(url, datos)
                                .then(() => {
                                    CosteCompraApp.mostrarAlerta('success', config.guardadoExitoso, config.cambiosGuardadosExito, 2000);
                                    fila.classList.remove('modificado');
                                    location.reload();
                                })
                                .catch(error => {
                                    CosteCompraApp.mostrarAlerta('error', config.error, config.errorGuardarCambios);
                                });
                        }
                    })
                    .catch(error => {
                        CosteCompraApp.mostrarAlerta('error', config.error, config.errorIdPedidoInvalido);
                    });
            });
        },

 validarExistenciaPedidoCompra: (idPedidoCompra) => {
            return middleware.get(`${config.pedidosCompraEndpoint}/${idPedidoCompra}/exists`)
                .then(data => data.existe)
                .catch(error => {
                    console.error('Error en la validación del ID del Pedido de Compra:', error);
                    return false;
                });
        },

        eliminarCosteCompra: (idCosteCompra) => {
            if (!idCosteCompra) {
                CosteCompraApp.mostrarAlerta('error', config.error, config.errorIdInvalido);
                return;
            }

            Swal.fire({
                title: config.confirmarEliminarCoste,
                text: config.confirmarEliminarCosteTexto,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: config.confirmarEliminar,
                cancelButtonText: config.cancelar
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = config.costesPedidosCompraIdEndpoint.replace('{id}', idCosteCompra);
                    middleware.delete(url)
                        .then(() => {
                            CosteCompraApp.mostrarAlerta('success', config.eliminado, config.costeEliminadoExito, 2000);
                            location.reload();
                        })
                        .catch(error => {
                            CosteCompraApp.mostrarAlerta('error', config.error, config.errorEliminarCoste);
                        });
                }
            });
        },

        crearCosteCompra: () => {
            const tbody = document.querySelector('tbody');
            const nuevaFila = document.createElement('tr');

            nuevaFila.innerHTML = `
                <td>
                    <button class="delete-button" onclick="CosteCompraApp.eliminarFila(this)">🗑️</button>
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

         // Función para verificar si un texto tiene el formato dd/MM/yyyy
         const isDateFormat = (text) => {
             return /^\d{2}\/\d{2}\/\d{4}$/.test(text);
         };

         // Función para convertir cadenas de fecha en formato dd/MM/yyyy a objetos Date
         const parseDate = (dateStr) => {
             const [day, month, year] = dateStr.split('/').map(Number);
             return new Date(year, month - 1, day); // Meses en JavaScript van de 0 a 11
         };

         rows.sort((a, b) => {
             const aText = a.children[columnIndex].innerText.trim();
             const bText = b.children[columnIndex].innerText.trim();

             // Si ambas son fechas, las ordenamos como fechas
             if (isDateFormat(aText) && isDateFormat(bText)) {
                 const aDate = parseDate(aText);
                 const bDate = parseDate(bText);
                 return isAscending ? aDate - bDate : bDate - aDate;
             }

             // Si no son fechas, hacer comparación de texto/numérico
             return isAscending
                 ? aText.localeCompare(bText, undefined, {numeric: true})
                 : bText.localeCompare(aText, undefined, {numeric: true});
         });

         // Reagregar las filas ordenadas al DOM
         rows.forEach(row => table.appendChild(row));

         // Actualizar la dirección de ordenamiento
         table.setAttribute("data-sort-direction", newDirection);

         // Ajustar los estilos visuales de los encabezados
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
                CosteCompraApp.filtrarCostes();
            }
        },

        toggleFilter: () => {
            const filterContainer = document.querySelector('.filter-container');
            filterContainer.classList.toggle('expanded');
        },

    filtrarCostes: () => {
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
    document.addEventListener('DOMContentLoaded', CosteCompraApp.initTableScroll());

    // Exponer globalmente
    window.CosteCompraApp = CosteCompraApp;
});
