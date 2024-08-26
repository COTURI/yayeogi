let airports = {};
let airlines = {};
let outboundFlights = [];
let inboundFlights = [];
let isSearchInProgress = false;
let outboundPage = 1;
let inboundPage = 1;
const flightsPerPage = 5; // 페이지당 항공편 수

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
    if (isNaN(date.getTime())) return '정보 없음';
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
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
                    inputElement.dataset.code = code;  // 공항 코드 저장
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

    showSpinner();  // 스피너 표시

    const originInput = document.getElementById('departure-city');
    const destinationInput = document.getElementById('arrival-city');
    const departureDateInput = document.getElementById('departure-date').value;
    const returnDateInput = document.getElementById('return-date').value;
    const adults = parseInt(document.getElementById('adults').value, 10);
    const origin = originInput.dataset.code || getAirportCode(originInput.value.trim()) || DEFAULT_AIRPORT_CODE;
    const destination = destinationInput.dataset.code || getAirportCode(destinationInput.value.trim()) || DEFAULT_AIRPORT_CODE;
    const formattedDepartureDate = formatDate(departureDateInput);
    const formattedReturnDate = formatDate(returnDateInput);

    // 필수 항목 검사
    if (!origin || !destination || !formattedDepartureDate) {
        const resultsSection = document.getElementById('results-container');
        if (resultsSection) resultsSection.innerHTML = '<p>모든 필수 항목을 입력해 주세요.</p>';
        isSearchInProgress = false;
        hideSpinner();  // 스피너 숨김
        return;
    }

    try {
        // 출국 및 귀국 항공편 요청
        let url = `/flights/search?origin=${encodeURIComponent(origin)}&destination=${encodeURIComponent(destination)}&departureDate=${encodeURIComponent(formattedDepartureDate)}&returnDate=${encodeURIComponent(formattedReturnDate)}&adults=${adults}`;
        console.log('Flight Search URL:', url);  // URL 확인
        const response = await fetch(url);
        if (!response.ok) throw new Error(`항공편 검색 오류! 상태: ${response.status}`);
        const data = await response.json();
        console.log('API Response Data:', data);  // 데이터 확인

        // 항공편 분리
        outboundFlights = [];
        inboundFlights = [];

        // 데이터 구조 확인 후 처리
        data.data.forEach(flightOffer => {
            const outboundItinerary = flightOffer.itineraries.find(itinerary =>
                itinerary.segments[0].departure.iataCode === origin &&
                itinerary.segments[0].arrival.iataCode === destination &&
                new Date(itinerary.segments[0].departure.at).toISOString().split('T')[0] === formattedDepartureDate
            );

            const inboundItinerary = flightOffer.itineraries.find(itinerary =>
                itinerary.segments[0].departure.iataCode === destination &&
                itinerary.segments[0].arrival.iataCode === origin &&
                new Date(itinerary.segments[0].departure.at).toISOString().split('T')[0] === formattedReturnDate
            );

            if (outboundItinerary) {
                outboundFlights.push({
                    id: flightOffer.id,
                    airline: flightOffer.validatingAirlineCodes[0] || DEFAULT_AIRLINE_CODE,
                    departureAirport: getAirportName(outboundItinerary.segments[0].departure.iataCode) || '정보 없음',
                    arrivalAirport: getAirportName(outboundItinerary.segments[0].arrival.iataCode) || '정보 없음',
                    departureTime: outboundItinerary.segments[0].departure.at,
                    arrivalTime: outboundItinerary.segments[0].arrival.at,
                    price: flightOffer.price.total,
                    date: formattedDepartureDate
                });
            }

            if (inboundItinerary) {
                inboundFlights.push({
                    id: flightOffer.id,
                    airline: flightOffer.validatingAirlineCodes[0] || DEFAULT_AIRLINE_CODE,
                    departureAirport: getAirportName(inboundItinerary.segments[0].departure.iataCode) || '정보 없음',
                    arrivalAirport: getAirportName(inboundItinerary.segments[0].arrival.iataCode) || '정보 없음',
                    departureTime: inboundItinerary.segments[0].departure.at,
                    arrivalTime: inboundItinerary.segments[0].arrival.at,
                    price: flightOffer.price.total,
                    date: formattedReturnDate
                });
            }
        });

        console.log('Outbound Flights:', outboundFlights);
        console.log('Inbound Flights:', inboundFlights);

        // 결과 업데이트
        const resultsSection = document.getElementById('results-container');
        if (resultsSection) {
            resultsSection.style.display = 'flex'; // 결과 컨테이너를 보이게 설정
            updateResults('outbound', outboundFlights, outboundPage);
            updateResults('inbound', inboundFlights, inboundPage);
        }
    } catch (error) {
        console.error('항공편 검색 오류:', error.message);
        const resultsSection = document.getElementById('results-container');
        if (resultsSection) resultsSection.innerHTML = `<p>항공편 검색 중 오류가 발생했습니다: ${error.message}</p>`;
    } finally {
        isSearchInProgress = false;
        hideSpinner();  // 스피너 숨김
    }
}


// 결과 업데이트 함수
function updateResults(type, flights, page) {
    const resultsSection = document.getElementById(`${type}-flights`);
    if (!resultsSection) {
        console.error(`Results section for ${type} not found.`);
        return;
    }

    resultsSection.innerHTML = '';

    const start = (page - 1) * flightsPerPage;
    const end = Math.min(start + flightsPerPage, flights.length);

    for (let i = start; i < end; i++) {
        const flight = flights[i];
        const flightElement = document.createElement('div');
        flightElement.classList.add('flight-item');
        flightElement.innerHTML = `
            <input type="radio" id="${type}-flight-${i}" name="${type}-flight" value="${flight.id}" />
            <label for="${type}-flight-${i}">
                ${getAirlineName(flight.airline)} - ${formatTime(flight.departureTime)} ~ ${formatTime(flight.arrivalTime)}
                (가격: ${flight.price})
            </label>
        `;
        resultsSection.appendChild(flightElement);
    }

    updatePagination(type, flights.length, page); // 페이지네이션 업데이트
    updateBookButtonState();  // 예약 버튼 상태 업데이트
}

// 체크박스 상태에 따라 예약 버튼 가시성 업데이트
function updateBookingButtonVisibility() {
    const outboundChecked = document.querySelector('input[name="outbound-flight"]:checked');
    const inboundChecked = document.querySelector('input[name="inbound-flight"]:checked');
    const bookingButton = document.getElementById('book-button');

    if (bookingButton) {
        // 출발 항공편과 귀국 항공편 모두 선택되어야 예약 버튼이 활성화됨
        bookingButton.disabled = !(outboundChecked && inboundChecked);
    }
}







// 스피너를 표시하는 함수
function showSpinner() {
    document.getElementById('spinner-container').style.display = 'flex';
}

function onCheckboxChange() {
    updateBookingButtonVisibility();
}

// 스피너를 숨기는 함수
function hideSpinner() {
    document.getElementById('spinner-container').style.display = 'none';
}

// 페이지네이션 버튼 생성 함수
function createPageButton(type, pageNumber, text) {
    const button = document.createElement('button');
    button.classList.add('pagination-button');
    button.textContent = text;
    button.disabled = (type === 'outbound' ? pageNumber === outboundPage : pageNumber === inboundPage);
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
    if (!paginationElement) return;

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




function updateBookButtonState() {
    const departureSelected = document.querySelector('input[name="outbound-flight"]:checked');
    const returnSelected = document.querySelector('input[name="inbound-flight"]:checked');
    const bookButton = document.getElementById('book-button');

    // 출국과 귀국 각각 체크된 항공편이 있을 때 버튼 활성화
    bookButton.disabled = !(departureSelected && returnSelected);
}

document.addEventListener('DOMContentLoaded', () => {
    loadData();

    // 라디오 버튼의 상태가 변경될 때마다 버튼 상태를 업데이트
    document.addEventListener('change', function(event) {
        if (event.target.matches('input[name="outbound-flight"]') ||
            event.target.matches('input[name="inbound-flight"]')) {
            updateBookButtonState();
        }
    });
});

// 검색 버튼 클릭 이벤트
document.getElementById('search-button').addEventListener('click', searchFlights);

// 예약 버튼 클릭 이벤트
document.getElementById('book-button').addEventListener('click', () => {
    const outboundFlightId = document.querySelector('input[name="outbound-flight"]:checked')?.value;
    const inboundFlightId = document.querySelector('input[name="inbound-flight"]:checked')?.value;

    if (outboundFlightId) {
        const outboundFlight = outboundFlights.find(flight => flight.id === outboundFlightId);
        const inboundFlight = inboundFlightId ? inboundFlights.find(flight => flight.id === inboundFlightId) : null;

        if (outboundFlight) {
            const params = new URLSearchParams({
                departureAirport: outboundFlight.departureAirport || DEFAULT_AIRPORT_CODE,
                arrivalAirport: outboundFlight.arrivalAirport || DEFAULT_AIRPORT_CODE,
                departureTime: outboundFlight.departureTime || '',
                arrivalTime: outboundFlight.arrivalTime || '',
                price: outboundFlight.price || '',
                carrierCode: outboundFlight.airline || DEFAULT_AIRLINE_CODE,
                returnDepartureAirport: inboundFlight ? inboundFlight.departureAirport || DEFAULT_AIRPORT_CODE : '',
                returnArrivalAirport: inboundFlight ? inboundFlight.arrivalAirport || DEFAULT_AIRPORT_CODE : '',
                returnDepartureTime: inboundFlight ? inboundFlight.departureTime || '' : '',
                returnArrivalTime: inboundFlight ? inboundFlight.arrivalTime || '' : '',
                returnPrice: inboundFlight ? inboundFlight.price || '' : '',
                returnCarrierCode: inboundFlight ? inboundFlight.airline || DEFAULT_AIRLINE_CODE : ''
            });

            // Remove empty parameters
            for (const [key, value] of params.entries()) {
                if (!value) {
                    params.delete(key);
                }
            }

            window.location.href = `indexmore?${params.toString()}`;
        } else {
            alert('출발 항공편을 선택해 주세요.');
        }
    } else {
        alert('출발 항공편을 선택해 주세요.');
    }
});

// 검색 입력 시 제안 업데이트
document.getElementById('departure-city').addEventListener('input', () => updateSuggestions('departure-city'));
document.getElementById('arrival-city').addEventListener('input', () => updateSuggestions('arrival-city'));

// 페이지 로드 시 데이터 로드
document.addEventListener('DOMContentLoaded', loadData);
