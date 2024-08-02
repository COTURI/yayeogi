let airports = {};
let airlines = {};
let outboundFlights = [];
let inboundFlights = [];
let isSearchInProgress = false;

function formatDate(dateInput) {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) {
        return null;
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function formatTime(dateInput) {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) {
        return null;
    }
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

function convertEuroToWon(euroAmount) {
    const exchangeRate = 1500;
    return (euroAmount * exchangeRate).toFixed(0);
}

async function loadData() {
    try {
        const airportResponse = await fetch('/json/airports.json');
        const airlineResponse = await fetch('/json/airlines.json');

        if (!airportResponse.ok || !airlineResponse.ok) {
            throw new Error('데이터를 불러오는 데 문제가 발생했습니다.');
        }

        airports = await airportResponse.json();
        airlines = await airlineResponse.json();

        console.log('Airports:', airports);
        console.log('Airlines:', airlines);
    } catch (error) {
        console.error('데이터 불러오기 오류:', error);
    }
}

function getAirportCode(name) {
    const airport = Object.entries(airports).find(([code, airportName]) =>
        airportName.toLowerCase() === name.toLowerCase()
    );
    return airport ? airport[0] : null;
}

function getAirportName(code) {
    return airports[code] || '알 수 없음';
}

function getAirlineName(code) {
    return airlines[code] || '알 수 없음';
}

function updateSuggestions(inputId) {
    const inputElement = document.getElementById(inputId);
    const suggestionsElement = document.getElementById(inputId + '-suggestions');
    const query = inputElement.value.toLowerCase();
    suggestionsElement.innerHTML = '';

    if (query.length > 1) {
        const suggestions = Object.entries(airports).filter(([code, name]) =>
            name.toLowerCase().includes(query) || code.toLowerCase().includes(query)
        );
        suggestions.forEach(([code, name]) => {
            const item = document.createElement('div');
            item.classList.add('suggestion-item');
            item.textContent = `${name} (${code})`;
            item.onclick = () => {
                inputElement.value = name;
                suggestionsElement.innerHTML = '';
            };
            suggestionsElement.appendChild(item);
        });

        if (suggestions.length === 0) {
            const item = document.createElement('div');
            item.classList.add('suggestion-item');
            item.textContent = '검색 결과가 없습니다.';
            suggestionsElement.appendChild(item);
        }
    }
}

async function searchFlights() {
    if (isSearchInProgress) return;
    isSearchInProgress = true;

    const originInput = document.getElementById('departure-city').value.trim();
    const destinationInput = document.getElementById('arrival-city').value.trim();
    const departureDateInput = document.getElementById('departure-date').value;
    const returnDateInput = document.getElementById('return-date').value;
    const adults = parseInt(document.getElementById('adults').value, 10);

    const origin = getAirportCode(originInput);
    const destination = getAirportCode(destinationInput);
    const formattedDepartureDate = formatDate(departureDateInput);
    const formattedReturnDate = formatDate(returnDateInput);

    if (!origin || !destination || !formattedDepartureDate) {
        document.getElementById('outbound-flights').innerHTML = '<p>필수 항목을 모두 입력해 주세요.</p>';
        document.getElementById('inbound-flights').innerHTML = '';
        isSearchInProgress = false;
        return;
    }

    try {
        const outboundUrl = `/flights?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}&departureDate=${encodeURIComponent(formattedDepartureDate)}&returnDate=${encodeURIComponent(formattedReturnDate || '')}&adults=${adults}`;
        const response = await fetch(outboundUrl);

        if (!response.ok) {
            throw new Error(`출발 항공편 검색 오류! 상태: ${response.status}`);
        }

        const data = await response.json();
        outboundFlights = data.outboundDeparture || [];
        inboundFlights = data.inboundReturn || [];

        console.log('Outbound Flights:', outboundFlights);
        console.log('Inbound Flights:', inboundFlights);

        updateResults('outbound', outboundFlights);
        updateResults('inbound', inboundFlights);

        document.getElementById('results-container').style.visibility = 'visible';
    } catch (error) {
        console.error('항공편 검색 오류:', error);
        document.getElementById('outbound-flights').innerHTML = '<p>항공편 검색 중 오류가 발생했습니다.</p>';
        document.getElementById('inbound-flights').innerHTML = '';
    } finally {
        isSearchInProgress = false;
    }
}

function updateResults(type, flights) {
    const container = document.getElementById(`${type}-flights`);
    if (!container) return;

    if (!flights || flights.length === 0) {
        container.innerHTML = '<p>검색 결과가 없습니다.</p>';
        return;
    }

    container.innerHTML = flights.map(flight => {
        const airlineName = flight.validatingAirlineCodes ? getAirlineName(flight.validatingAirlineCodes[0]) : '정보 없음';
        const priceEuro = flight.price ? flight.price.grandTotal : '정보 없음';
        const priceWon = flight.price ? convertEuroToWon(parseFloat(flight.price.grandTotal)) : '정보 없음';
        const departure = flight.itineraries && flight.itineraries[0] && flight.itineraries[0].segments && flight.itineraries[0].segments[0]
            ? `${getAirportName(flight.itineraries[0].segments[0].departure.iataCode)} ${formatDate(flight.itineraries[0].segments[0].departure.at)} ${formatTime(flight.itineraries[0].segments[0].departure.at)}`
            : '정보 없음';
        const arrival = flight.itineraries && flight.itineraries[0] && flight.itineraries[0].segments && flight.itineraries[0].segments[0]
            ? `${getAirportName(flight.itineraries[0].segments[0].arrival.iataCode)} ${formatDate(flight.itineraries[0].segments[0].arrival.at)} ${formatTime(flight.itineraries[0].segments[0].arrival.at)}`
            : '정보 없음';

        return `
            <div class="flight">
                <p>항공사: ${airlineName}</p>
                <p>가격: ${priceEuro} EUR (${priceWon} 원)</p>
                <p>출발: ${departure}</p>
                <p>도착: ${arrival}</p>
            </div>
        `;
    }).join('');
}

document.getElementById('search-button').addEventListener('click', searchFlights);
document.getElementById('departure-city').addEventListener('input', () => updateSuggestions('departure-city'));
document.getElementById('arrival-city').addEventListener('input', () => updateSuggestions('arrival-city'));

document.addEventListener('DOMContentLoaded', loadData);
