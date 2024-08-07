document.getElementById('hotelSearchForm').addEventListener('submit', async function(event) {
    event.preventDefault();
    const cityCode = document.getElementById('city').value;

    try {
        const response = await fetch(`http://localhost:8080/api/hotels?cityCode=${cityCode}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch hotel data: ${response.statusText}`);
        }
        const data = await response.json();
        displayHotels(data);
    } catch (error) {
        console.error('Error fetching hotel data:', error);
        document.getElementById('hotelResults').textContent = 'An error occurred while fetching data.';
    }
});

function displayHotels(hotels) {
    const resultsContainer = document.getElementById('hotelResults');
    resultsContainer.innerHTML = ''; // Clear previous results

    if (hotels && hotels.length) {
        hotels.forEach(offer => {
            const name = offer.hotel.name;
            const address = offer.hotel.address.lines.join(', ');

            const hotelElement = document.createElement('div');
            hotelElement.innerHTML = `<strong>${name}</strong><br>${address}<br><br>`;
            resultsContainer.appendChild(hotelElement);
        });
    } else {
        resultsContainer.textContent = 'No hotels found for the specified city.';
    }
}
}



