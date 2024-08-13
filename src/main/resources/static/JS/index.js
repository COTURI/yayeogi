let airports = {};
let airlines = {};
let isSearchInProgress = false;
const flightsPerPage = 5;
let outboundFlights = [];
let inboundFlights = [];
let outboundPage = 1;
let inboundPage = 1;
const currency = 'EUR'; // 기본 통화 설정

function decodeHtmlEntities(text) {
    const textarea = document.createElement('textarea');
    textarea.innerHTML = text;
    return textarea.value;
}

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
        return '정보 없음';
    }
    const hours = String(date.getUTCHours()).padStart(2, '0');
    const minutes = String(date.getUTCMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

function convertEuroToWon(euroAmount) {
    const exchangeRate = 1500; // 원화 환율 (예시)
    let wonAmount = euroAmount * exchangeRate;
    wonAmount = Math.floor(wonAmount / 100) * 100;
    return wonAmount;
}

async function loadData() {
    if (Object.keys(airports).length > 0 && Object.keys(airlines).length > 0) {
        console.log('데이터가 이미 로드되었습니다.');
        return;
    }
    try {
        const [airportResponse, airlineResponse] = await Promise.all([
            fetch('/json/airports.json'),
            fetch('/json/airlines.json')
        ]);
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
    return airports[code] || '정보 없음';
}

function getAirlineName(code) {
    return airlines[code] || '정보 없음';
}

function updateSuggestions(inputId) {
    const inputElement = document.getElementById(inputId);
    const suggestionsElement = document.getElementById(inputId + '-suggestions');
    const query = inputElement.value.trim().toLowerCase();
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
                inputElement.focus();
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
    console.log('검색 시작');
    if (isSearchInProgress) return;
    isSearchInProgress = true;
    document.getElementById('spinner').style.display = 'flex';
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
        document.getElementById('spinner').style.display = 'none';
        return;
    }
    try {
        const outboundUrl = `/flights?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}&departureDate=${encodeURIComponent(formattedDepartureDate)}&returnDate=${encodeURIComponent(formattedReturnDate || '')}&adults=${adults}&currency=${currency}`;
        const response = await fetch(outboundUrl);
        if (!response.ok) {
            throw new Error(`출발 항공편 검색 오류! 상태: ${response.status}`);
        }
        const data = await response.json();
        outboundFlights = data.outboundDeparture || [];
        inboundFlights = data.inboundReturn || [];
        console.log('Outbound Flights:', outboundFlights);
        console.log('Inbound Flights:', inboundFlights);
        updateResults('outbound', outboundFlights, outboundPage);
        updateResults('inbound', inboundFlights, inboundPage);
        document.getElementById('results-container').style.display = 'block';
        document.getElementById('book-button').style.display = outboundFlights.length > 0 ? 'block' : 'none';
    } catch (error) {
        console.error('항공편 검색 오류:', error);
        document.getElementById('outbound-flights').innerHTML = '<p>항공편 검색 중 오류가 발생했습니다.</p>';
        document.getElementById('inbound-flights').innerHTML = '';
    } finally {
        isSearchInProgress = false;
        document.getElementById('spinner').style.display = 'none';
    }
}

function updateResults(type, flights, page) {
    const container = document.getElementById(`${type}-flights`);
    if (!container) {
        console.error(`결과 컨테이너를 찾을 수 없습니다: ${type}-flights`);
        return;
    }
    const startIndex = (page - 1) * flightsPerPage;
    const endIndex = startIndex + flightsPerPage;
    const flightsToDisplay = flights.slice(startIndex, endIndex);
    if (!flightsToDisplay || flightsToDisplay.length === 0) {
        container.innerHTML = '<p>검색 결과가 없습니다.</p>';
        return;
    }
    const resultsHtml = flightsToDisplay.map(flight => {
        const itineraries = flight.itineraries[0] || {};
        const segments = itineraries.segments || [];
        console.log('Flight segments:', segments);
        const departureAirportCode = segments.length > 0 ? segments[0].departure.iataCode : '정보 없음';
        const arrivalAirportCode = segments.length > 0 ? segments[segments.length - 1].arrival.iataCode : '정보 없음';
        const departureTime = segments.length > 0 ? formatTime(segments[0].departure.at) : '정보 없음';
        const arrivalTime = segments.length > 0 ? formatTime(segments[segments.length - 1].arrival.at) : '정보 없음';
        const priceInWon = convertEuroToWon(parseFloat(flight.price.total) || 0);
        const airlineName = getAirlineName(flight.validatingAirlineCodes[0] || '정보 없음');
        const flightId = flight.id || '';
        return `
            <div class="flight-item">
                <input type="radio" name="flight-${type}" class="flight-radio" data-id="${flightId}" data-type="${type}"
                    data-departure-airport="${getAirportName(departureAirportCode)}"
                    data-arrival-airport="${getAirportName(arrivalAirportCode)}"
                    data-departure-time="${departureTime}"
                    data-arrival-time="${arrivalTime}"
                    data-price="${flight.price.total}"
                    data-carrier-code="${flight.validatingAirlineCodes[0] || '정보 없음'}">
                <div class="flight-info">
                    <p><strong>${getAirportName(departureAirportCode)}</strong> → <strong>${getAirportName(arrivalAirportCode)}</strong></p>
                    <p>출발: ${departureTime} / 도착: ${arrivalTime}</p>
                    <p>항공사: ${airlineName}</p>
                    <p>가격: €${flight.price.total} (${priceInWon}원)</p>
                </div>
            </div>
        `;
    }).join('');
    container.innerHTML = resultsHtml;
    updatePagination(type, flights.length, page);
}

function updatePagination(type, totalFlights, currentPage) {
    const paginationContainer = document.getElementById(`${type}-pagination`);
    if (!paginationContainer) return;
    const totalPages = Math.ceil(totalFlights / flightsPerPage);
    let paginationHtml = '';
    for (let i = 1; i <= totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<span class="pagination-item active">${i}</span>`;
        } else {
            paginationHtml += `<span class="pagination-item" onclick="changePage('${type}', ${i})">${i}</span>`;
        }
    }
    paginationContainer.innerHTML = paginationHtml;
}

function changePage(type, page) {
    if (type === 'outbound') {
        outboundPage = page;
        updateResults('outbound', outboundFlights, outboundPage);
    } else if (type === 'inbound') {
        inboundPage = page;
        updateResults('inbound', inboundFlights, inboundPage);
    }
}


document.getElementById('book-button').addEventListener('click', () => {
    const selectedOutboundFlight = document.querySelector('#outbound-flights .flight-radio:checked');
    const selectedInboundFlight = document.querySelector('#inbound-flights .flight-radio:checked');
    if (!selectedOutboundFlight) {
        alert('출발 항공편을 선택해 주세요.');
        return;
    }
    const encodeIfNotEmpty = (value) => value ? encodeURIComponent(value) : '';
    const outboundFlightId = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-id'));
    const inboundFlightId = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-id')) : '';
    const departureAirport = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-departure-airport'));
    const arrivalAirport = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-arrival-airport'));
    const departureTime = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-departure-time'));
    const arrivalTime = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-arrival-time'));
    const price = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-price'));
    const carrierCode = encodeIfNotEmpty(selectedOutboundFlight.getAttribute('data-carrier-code'));
    const returnDepartureAirport = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-departure-airport')) : '';
    const returnArrivalAirport = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-arrival-airport')) : '';
    const returnDepartureTime = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-departure-time')) : '';
    const returnArrivalTime = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-arrival-time')) : '';
    const returnPrice = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-price')) : '';
    const returnCarrierCode = selectedInboundFlight ? encodeIfNotEmpty(selectedInboundFlight.getAttribute('data-carrier-code')) : '';
    const params = new URLSearchParams();
    if (outboundFlightId) params.append('flightId', outboundFlightId);
    if (inboundFlightId) params.append('returnFlightId', inboundFlightId);
    if (departureAirport) params.append('departureAirport', departureAirport);
    if (arrivalAirport) params.append('arrivalAirport', arrivalAirport);
    if (departureTime) params.append('departureTime', departureTime);
    if (arrivalTime) params.append('arrivalTime', arrivalTime);
    if (price) params.append('price', price);
    if (carrierCode) params.append('carrierCode', carrierCode);
    if (returnDepartureAirport) params.append('returnDepartureAirport', returnDepartureAirport);
    if (returnArrivalAirport) params.append('returnArrivalAirport', returnArrivalAirport);
    if (returnDepartureTime) params.append('returnDepartureTime', returnDepartureTime);
    if (returnArrivalTime) params.append('returnArrivalTime', returnArrivalTime);
    if (returnPrice) params.append('returnPrice', returnPrice);
    if (returnCarrierCode) params.append('returnCarrierCode', returnCarrierCode);
    params.append('currency', currency);
    const baseUrl = '/indexmore';
    const indexmoreUrl = new URL(baseUrl, window.location.origin);
    indexmoreUrl.search = params.toString();
    console.log('Generated URL:', indexmoreUrl.toString());
    window.location.href = indexmoreUrl.toString();
});

document.addEventListener('DOMContentLoaded', () => {
    const searchButton = document.getElementById('search-button');
    const departureCityInput = document.getElementById('departure-city');
    const arrivalCityInput = document.getElementById('arrival-city');
    const resultsContainer = document.getElementById('results-container');
    const bookButton = document.getElementById('book-button');
    const spinner = document.getElementById('spinner');

    if (searchButton) {
        searchButton.addEventListener('click', () => {
            searchFlights();
        });
    }

    if (departureCityInput) {
        departureCityInput.addEventListener('input', () => updateSuggestions('departure-city'));
    }

    if (arrivalCityInput) {
        arrivalCityInput.addEventListener('input', () => updateSuggestions('arrival-city'));
    }

    if (bookButton) {
        bookButton.style.display = 'none';
    }



    loadData();
});
