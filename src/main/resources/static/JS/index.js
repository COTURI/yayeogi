let airports = {};
let airlines = {};
let outboundFlights = [];
let inboundFlights = [];
let isSearchInProgress = false;
let outboundPage = 1;
let inboundPage = 1;
const flightsPerPage = 5;

// 날짜를 Amadeus API에 맞는 형식으로 변환하는 함수
function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 시간을 형식화하는 함수
function formatTime(dateInput) {
    const date = new Date(dateInput);
    if (isNaN(date.getTime())) {
        return '정보 없음';
    }
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
}

// 쿠키에서 값 가져오기
function getCookie(name) {
    const matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

// 헤더 업데이트 함수
function updateHeader() {
    const loggedIn = getCookie('loggedin') === 'true';
    const username = getCookie('username');
    const authSection = document.querySelector('.auth');

    if (loggedIn) {
        authSection.innerHTML =
            `<button type="button" class="btn btn-secondary">
                <img src="/html/layout/favorite.png" alt="찜"> 찜
            </button>
            <button type="button" class="btn btn-secondary">
                <img src="/html/layout/user.png" alt="내정보"> ${username}님
            </button>
            <button type="button" class="btn btn-secondary" onclick="logout()">
                <img src="/html/layout/logout.png" alt="로그아웃"> 로그아웃
            </button>`;
    } else {
        authSection.innerHTML =
            `<button type="button" class="btn btn-secondary" onclick="location.href='/register'">
                회원가입
            </button>
            <button type="button" class="btn btn-secondary" onclick="location.href='/login'">
                로그인
            </button>`;
    }
}

// 로그아웃 함수
function logout() {
    document.cookie = "loggedin=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;";
    document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/;";
    location.reload();
}

// 데이터 로드 함수
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

        if (!airportResponse.ok) throw new Error('공항 데이터 요청 실패');
        if (!airlineResponse.ok) throw new Error('항공사 데이터 요청 실패');

        airports = await airportResponse.json();
        airlines = await airlineResponse.json();

        console.log('Airports:', airports);
        console.log('Airlines:', airlines);
    } catch (error) {
        console.error('데이터 불러오기 오류:', error);
        document.getElementById('results-container').innerHTML = `<p>데이터 로드 중 오류가 발생했습니다: ${error.message}</p>`;
    }
}

// 공항 코드 가져오기
function getAirportCode(name) {
    const airport = Object.entries(airports).find(([code, airportName]) =>
        airportName.toLowerCase() === name.toLowerCase()
    );
    return airport ? airport[0] : null;
}

// 공항 이름 가져오기
function getAirportName(code) {
    return airports[code] || '정보 없음';
}

// 항공사 이름 가져오기
function getAirlineName(code) {
    return airlines[code] || '정보 없음';
}

// 검색 제안 업데이트
function updateSuggestions(inputId) {
    const inputElement = document.getElementById(inputId);
    const suggestionsElement = document.getElementById(`${inputId}-suggestions`);
    const query = inputElement.value.trim().toLowerCase();

    if (!suggestionsElement) {
        console.error(`Suggestions element with id ${inputId + '-suggestions'} not found.`);
        return;
    }

    suggestionsElement.innerHTML = '';
    if (query.length > 1) {
        const suggestions = Object.entries(airports).filter(([code, name]) =>
            name.toLowerCase().includes(query) || code.toLowerCase().includes(query)
        );

        if (suggestions.length > 0) {
            suggestions.forEach(([code, name]) => {
                const item = document.createElement('div');
                item.classList.add('suggestion-item');
                item.textContent = `${name} (${code})`;
                item.onclick = () => {
                    inputElement.value = name;
                    suggestionsElement.innerHTML = '';
                    suggestionsElement.style.display = 'none'; // 제안 상자를 숨김
                };
                suggestionsElement.appendChild(item);
            });
        } else {
            const item = document.createElement('div');
            item.classList.add('suggestion-item');
            item.textContent = '검색 결과가 없습니다.';
            suggestionsElement.appendChild(item);
        }

        suggestionsElement.style.display = 'block';
    } else {
        suggestionsElement.style.display = 'none';
    }
}

// 항공편 검색 함수
async function searchFlights() {
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
        // URL 생성
        let outboundUrl = `/flights/search?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}&departureDate=${encodeURIComponent(formattedDepartureDate)}&adults=${adults}`;
        if (returnDateInput) {
            outboundUrl += `&returnDate=${encodeURIComponent(formattedReturnDate)}`;
        }
        const response = await fetch(outboundUrl);
        if (!response.ok) throw new Error(`출발 항공편 검색 오류! 상태: ${response.status}`);

        const data = await response.json();
        outboundFlights = data.outboundDeparture || [];
        inboundFlights = data.inboundReturn || [];

        // 추가된 로그
        console.log('Outbound Flights:', outboundFlights);
        console.log('Inbound Flights:', inboundFlights);

        updateResults('outbound', outboundFlights, outboundPage);
        updateResults('inbound', inboundFlights, inboundPage);
        document.getElementById('results-container').style.visibility = 'visible';
    } catch (error) {
        console.error('항공편 검색 오류:', error.message);
        document.getElementById('outbound-flights').innerHTML = `<p>항공편 검색 중 오류가 발생했습니다: ${error.message}</p>`;
        document.getElementById('inbound-flights').innerHTML = '';
    } finally {
        isSearchInProgress = false;
        document.getElementById('spinner').style.display = 'none';
    }
}

// 결과 업데이트 함수
function updateResults(type, flights, page) {
    const resultsSection = document.getElementById(`${type}-flights`);
    resultsSection.innerHTML = '';

    if (flights.length === 0) {
        resultsSection.innerHTML = `<p>검색 결과가 없습니다.</p>`;
        return;
    }

    const start = (page - 1) * flightsPerPage;
    const end = Math.min(start + flightsPerPage, flights.length);

    for (let i = start; i < end; i++) {
        const flight = flights[i];
        const flightElement = document.createElement('div');
        flightElement.classList.add('flight-item');
        flightElement.innerHTML = `
            <p>항공사: ${getAirlineName(flight.airline)}</p>
            <p>출발: ${formatTime(flight.departureTime)}</p>
            <p>도착: ${formatTime(flight.arrivalTime)}</p>
            <p>가격: ${flight.price}</p>
        `;
        resultsSection.appendChild(flightElement);
    }

    updatePagination(type, flights.length, page);
}

// 페이지네이션 버튼 생성 함수
function createPageButton(type, pageNumber, text) {
    const button = document.createElement('button');
    button.classList.add('pagination-button');
    button.textContent = text;
    button.disabled = pageNumber === (type === 'outbound' ? outboundPage : inboundPage);
    button.onclick = () => {
        if (type === 'outbound') {
            outboundPage = pageNumber;
            updateResults('outbound', outboundFlights, outboundPage);
        } else {
            inboundPage = pageNumber;
            updateResults('inbound', inboundFlights, inboundPage);
        }
    };
    return button;
}

// 페이지네이션 업데이트 함수
function updatePagination(type, totalFlights, page) {
    const paginationElement = document.getElementById(`pagination-${type}`);
    paginationElement.innerHTML = '';

    const totalPages = Math.ceil(totalFlights / flightsPerPage);

    if (totalPages <= 1) return;

    if (page > 1) {
        paginationElement.appendChild(createPageButton(type, page - 1, '« 이전'));
    }

    for (let i = 1; i <= totalPages; i++) {
        paginationElement.appendChild(createPageButton(type, i, i));
    }

    if (page < totalPages) {
        paginationElement.appendChild(createPageButton(type, page + 1, '다음 »'));
    }
}

// 검색 버튼 클릭 이벤트
document.getElementById('search-button').addEventListener('click', searchFlights);

// 검색 입력 시 제안 업데이트
document.getElementById('departure-city').addEventListener('input', () => updateSuggestions('departure-city'));
document.getElementById('arrival-city').addEventListener('input', () => updateSuggestions('arrival-city'));



// 페이지 로드 시 데이터 로드
document.addEventListener('DOMContentLoaded', loadData);
