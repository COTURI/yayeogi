function searchHotels() {
    const cityCode = document.getElementById('cityCode').value;
    const checkInDate = document.getElementById('checkInDate').value;
    const checkOutDate = document.getElementById('checkOutDate').value;
    const adults = document.getElementById('adults').value;

    fetch(`/api/hotels/search?cityCode=${cityCode}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&adults=${adults}`)
        .then(response => response.json())
        .then(data => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';

            if (data && data.length > 0) {
                data.forEach(offer => {
                    const hotelInfo = `
                        <div>
                            <h2>${offer.hotel.name}</h2>
                            <p>${offer.hotel.address.lines.join(', ')}, ${offer.hotel.address.cityName}</p>
                            <p>Price: ${offer.offers[0].price.total} ${offer.offers[0].price.currency}</p>
                        </div>
                        <hr/>
                    `;
                    resultsDiv.innerHTML += hotelInfo;
                });
            } else {
                resultsDiv.innerHTML = 'No results found.';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('results').innerHTML = 'Error fetching results.';
        });
}
