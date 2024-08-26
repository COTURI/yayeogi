let airlines = {};
let airports = {};

// 유로를 원화로 변환하는 함수
function convertEuroToWon(euroAmount) {
    const exchangeRate = 1500; // 예시 환율
    return (euroAmount * exchangeRate).toFixed(0); // 원화로 변환
}

// URL 디코딩 함수
function decodeURIComponentSafe(encodedUri) {
    try {
        return decodeURIComponent(encodedUri);
    } catch (e) {
        console.error('URL 디코딩 오류:', e);
        return encodedUri;
    }
}

// 데이터 로드 함수
async function loadData() {
    try {
        const [airportsResponse, airlinesResponse] = await Promise.all([
            fetch('/json/airports.json'),
            fetch('/json/airlines.json')
        ]);

        if (!airportsResponse.ok || !airlinesResponse.ok) {
            throw new Error('데이터 로드 실패');
        }

        airports = await airportsResponse.json();
        airlines = await airlinesResponse.json();
        displayBookingDetails();
    } catch (error) {
        console.error('데이터 로드 오류:', error);
    }
}

// 쿼리 파라미터 가져오기
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    const value = decodeURIComponentSafe(urlParams.get(param));
    console.log(`Query param [${param}]: ${value}`); // 디버깅용 출력
    return value || '정보 없음';
}

// 예약 상세 정보 표시
function displayBookingDetails() {
    const departureAirport = getQueryParam('departureAirport');
    const arrivalAirport = getQueryParam('arrivalAirport');
    const departureTime = getQueryParam('departureTime');
    const arrivalTime = getQueryParam('arrivalTime') || '정보 없음';
    const price = getQueryParam('price');
    const carrierCode = getQueryParam('carrierCode');
    const returnDepartureAirport = getQueryParam('returnDepartureAirport');
    const returnArrivalAirport = getQueryParam('returnArrivalAirport');
    const returnDepartureTime = getQueryParam('returnDepartureTime');
    const returnArrivalTime = getQueryParam('returnArrivalTime') || '정보 없음';
    const returnPrice = getQueryParam('returnPrice');
    const returnCarrierCode = getQueryParam('returnCarrierCode');

    const outboundPriceWon = convertEuroToWon(parseFloat(price));
    const inboundPriceWon = convertEuroToWon(parseFloat(returnPrice));

    document.getElementById('booking-details').innerHTML = `
        <h2>출발 항공편</h2>
        <p><strong>출발 공항:</strong> ${decodeURIComponentSafe(departureAirport)}</p>
        <p><strong>도착 공항:</strong> ${decodeURIComponentSafe(arrivalAirport)}</p>
        <p><strong>출발 시간:</strong> ${departureTime}</p>
        <p><strong>도착 시간:</strong> ${arrivalTime}</p>
        <p><strong>가격:</strong> ₩${outboundPriceWon}</p>
        <p><strong>항공사:</strong> ${airlines[carrierCode] || '정보 없음'}</p>
        <h2>귀국 항공편</h2>
        <p><strong>출발 공항:</strong> ${decodeURIComponentSafe(returnDepartureAirport)}</p>
        <p><strong>도착 공항:</strong> ${decodeURIComponentSafe(returnArrivalAirport)}</p>
        <p><strong>출발 시간:</strong> ${returnDepartureTime}</p>
        <p><strong>도착 시간:</strong> ${returnArrivalTime}</p>
        <p><strong>가격:</strong> ₩${inboundPriceWon}</p>
        <p><strong>항공사:</strong> ${airlines[returnCarrierCode] || '정보 없음'}</p>
    `;
}

async function loadAirlines() {
    try {
        const response = await fetch('/json/airlines.json');
        if (!response.ok) throw new Error('데이터를 불러오는 데 문제가 발생했습니다.');
        return await response.json();
    } catch (error) {
        console.error('데이터 불러오기 오류:', error);
        return {};
    }
}
document.addEventListener('DOMContentLoaded', async () => {
    airlines = await loadAirlines();
    displayBookingDetails();
});


document.addEventListener('DOMContentLoaded', () => {
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        const value = urlParams.get(param);
        return value ? decodeURIComponent(value) : '정보 없음';
    }

    function displayBookingDetails() {
        const departureAirport = getQueryParam('departureAirport');
        const arrivalAirport = getQueryParam('arrivalAirport');
        const departureTime = getQueryParam('departureTime');
        const arrivalTime = getQueryParam('arrivalTime') || '정보 없음'; // 기본값 설정
        const returnDepartureAirport = getQueryParam('returnDepartureAirport');
        const returnArrivalAirport = getQueryParam('returnArrivalAirport');
        const returnDepartureTime = getQueryParam('returnDepartureTime');
        const returnArrivalTime = getQueryParam('returnArrivalTime') || '정보 없음'; // 기본값 설정
        const price = getQueryParam('price');
        const returnPrice = getQueryParam('returnPrice');
        const carrierCode = getQueryParam('carrierCode');
        const returnCarrierCode = getQueryParam('returnCarrierCode');

        const bookingDetails = `
            <h2>출발 항공편</h2>
            <p><strong>출발 공항:</strong> ${departureAirport}</p>
            <p><strong>도착 공항:</strong> ${arrivalAirport}</p>
            <p><strong>출발 시간:</strong> ${departureTime}</p>
            <p><strong>도착 시간:</strong> ${arrivalTime}</p>
            <p><strong>가격:</strong> ${price} EUR</p>
            <p><strong>항공사:</strong> ${getAirlineName(carrierCode)}</p>

            <h2>귀국 항공편</h2>
            <p><strong>출발 공항:</strong> ${returnDepartureAirport}</p>
            <p><strong>도착 공항:</strong> ${returnArrivalAirport}</p>
            <p><strong>출발 시간:</strong> ${returnDepartureTime}</p>
            <p><strong>도착 시간:</strong> ${returnArrivalTime}</p>
            <p><strong>가격:</strong> ${returnPrice} EUR</p>
            <p><strong>항공사:</strong> ${getAirlineName(returnCarrierCode)}</p>
        `;

        document.getElementById('booking-details').innerHTML = bookingDetails;
    }

    function getAirlineName(code) {
        const airlines = {}; // 항공사 데이터는 적절히 로드해야 합니다.
        // airlines 데이터 로드 코드 추가 (예: 서버에서 받아오는 방식)
        return airlines[code] || '정보 없음';
    }

    displayBookingDetails();
});
