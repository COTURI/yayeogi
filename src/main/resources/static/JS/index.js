let airports = {};
let airlines = {};
let outboundFlights = [];
let inboundFlights = [];
let isSearchInProgress = false;
let outboundPage = 1;
let inboundPage = 1;
const flightsPerPage = 5;

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
    if (Object.keys(airports).length > 0 && Object.keys(airlines).length > 0) {
        // 이미 데이터가 로드된 경우, 함수 종료
        console.log('데이터가 이미 로드되었습니다.');
        return;
    }

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
    console.log('검색 시작'); // 디버깅을 위한 로그 추가
    if (isSearchInProgress) return;
    isSearchInProgress = true;

    // 스피너 표시
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
        document.getElementById('spinner').style.display = 'none'; // 스피너 숨기기
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

        updateResults('outbound', outboundFlights, outboundPage);
        updateResults('inbound', inboundFlights, inboundPage);

        document.getElementById('results-container').style.visibility = 'visible';
    } catch (error) {
        console.error('항공편 검색 오류:', error);
        document.getElementById('outbound-flights').innerHTML = '<p>항공편 검색 중 오류가 발생했습니다.</p>';
        document.getElementById('inbound-flights').innerHTML = '';
    } finally {
        isSearchInProgress = false;
        document.getElementById('spinner').style.display = 'none'; // 스피너 숨기기
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
        const departureTime = segments.length > 0 ? formatTime(segments[0].departure.at) : '알 수 없음';
        const arrivalTime = segments.length > 0 ? formatTime(segments[segments.length - 1].arrival.at) : '알 수 없음';
        const priceInWon = convertEuroToWon(parseFloat(flight.price.total) || 0);
        const airlineName = getAirlineName(flight.validatingAirlineCodes[0] || '알 수 없음');

        return `
            <div class="flight-item">
                <div class="flight-info">
                    <p><strong>${getAirportName(flight.itineraries[0]?.segments[0]?.departure?.iataCode || '알 수 없음')}</strong> → <strong>${getAirportName(flight.itineraries[0]?.segments[segments.length - 1]?.arrival?.iataCode || '알 수 없음')}</strong></p>
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
    const totalPages = Math.ceil(totalFlights / flightsPerPage);
    const paginationContainer = document.getElementById(`pagination-${type}`);

    if (!paginationContainer) return;

    paginationContainer.innerHTML = '';

    if (totalPages <= 1) return;

    const createPageButton = (page, text, isActive = false) => {
        const button = document.createElement('button');
        button.textContent = text;
        button.className = isActive ? 'active' : '';
        button.disabled = isActive;
        button.onclick = () => {
            if (type === 'outbound') {
                outboundPage = page;
            } else {
                inboundPage = page;
            }
            searchFlights(); // 페이지 버튼 클릭 시 검색 실행
        };
        return button;
    };

    // Previous Button
    if (currentPage > 1) {
        paginationContainer.appendChild(createPageButton(currentPage - 1, '◀️'));
    }

    // Page Numbers
    for (let i = 1; i <= totalPages; i++) {
        paginationContainer.appendChild(createPageButton(i, i, i === currentPage));
    }

    // Next Button
    if (currentPage < totalPages) {
        paginationContainer.appendChild(createPageButton(currentPage + 1, '▶️'));
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadData();
    document.getElementById('search-button').addEventListener('click', searchFlights);
    document.getElementById('departure-city').addEventListener('input', () => updateSuggestions('departure-city'));
    document.getElementById('arrival-city').addEventListener('input', () => updateSuggestions('arrival-city'));
});

