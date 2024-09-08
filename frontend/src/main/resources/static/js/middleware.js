const middleware = {
    get: (url) => {
        return fetch(url, {
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
        return fetch(url, {
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
        return fetch(url, {
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
        return fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                return Promise.resolve();
            } else {
                return Promise.reject(new Error('Error en la solicitud DELETE.'));
            }
        })
        .catch(error => {
            console.error(`Error en la solicitud DELETE a ${url}:`, error);
            throw error;
        });
    }
};

export default middleware;
