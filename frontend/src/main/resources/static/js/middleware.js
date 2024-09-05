const API_BASE_URL = 'http://localhost:8702/api/compras';

// Middleware para manejar las peticiones
const middleware = {
    get: (url) => {
        return fetch(`${API_BASE_URL}${url}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .catch(error => {
            console.error(`Error en la solicitud GET a ${url}:`, error);
            throw error;
        });
    },

    post: (url, data) => {
        return fetch(`${API_BASE_URL}${url}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(response => response.json())
        .catch(error => {
            console.error(`Error en la solicitud POST a ${url}:`, error);
            throw error;
        });
    },

    put: (url, data) => {
        return fetch(`${API_BASE_URL}${url}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
        .then(response => response.json())
        .catch(error => {
            console.error(`Error en la solicitud PUT a ${url}:`, error);
            throw error;
        });
    },

delete: (url) => {
        return fetch(`${API_BASE_URL}${url}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            // Solo intentar convertir a JSON si hay contenido
            if (response.status !== 204) {
                return response.json();
            }
            // Para respuestas 204 No Content, no se debe intentar parsear JSON
            return Promise.resolve();
        })
        .catch(error => {
            console.error(`Error en la solicitud DELETE a ${url}:`, error);
            throw error;
        });
    }
};

// Exportar el middleware para usarlo en otras partes del proyecto
export default middleware;
