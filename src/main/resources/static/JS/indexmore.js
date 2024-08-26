let airports = {};
let airlines = {};

function convertEuroToWon(euroAmount) {
    const exchangeRate = 1500; // 원화 환율 (예시)
    let wonAmount = euroAmount * exchangeRate;
    wonAmount = Math.floor(wonAmount / 1000) * 1000;
    return wonAmount; // 소수점 없이 원화로 변환
}

async function loadData() {
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
    } catch (error) {
        console.error('데이터 불러오기 오류:', error);
    }
}

function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    const value = urlParams.get(param);
    return value ? decodeURIComponent(value) : '정보 없음';
}
function getAirlineName(code) {
    return airlines[code] || '정보 없음';
}

function displayBookingDetails() {
    const outboundDepartureAirportName = getQueryParam('departureAirport');
    const outboundArrivalAirportName = getQueryParam('arrivalAirport');
    const outboundDepartureTime = getQueryParam('departureTime');
    const outboundArrivalTime = getQueryParam('arrivalTime') || '정보 없음'; // 기본값 설정
    const outboundPrice = getQueryParam('price');
    const outboundCarrierCode = getQueryParam('carrierCode');
    const inboundDepartureAirportName = getQueryParam('returnDepartureAirport');
    const inboundArrivalAirportName = getQueryParam('returnArrivalAirport');
    const inboundDepartureTime = getQueryParam('returnDepartureTime');
    const inboundArrivalTime = getQueryParam('returnArrivalTime') || '정보 없음'; // 기본값 설정
    const inboundPrice = getQueryParam('returnPrice');
    const inboundCarrierCode = getQueryParam('returnCarrierCode');

    // 가격을 원화로 변환
    const outboundPriceWon = convertEuroToWon(parseFloat(outboundPrice));
    const inboundPriceWon = convertEuroToWon(parseFloat(inboundPrice));

    const bookingDetails = `
        <h2>출발 항공편</h2>
        <p><strong>출발 공항:</strong> ${outboundDepartureAirportName}</p>
        <p><strong>도착 공항:</strong> ${outboundArrivalAirportName}</p>
        <p><strong>출발 시간:</strong> ${outboundDepartureTime}</p>
        <p><strong>도착 시간:</strong> ${outboundArrivalTime}</p>
        <p><strong>가격:</strong> ₩${outboundPriceWon}</p>
        <p><strong>항공사:</strong> ${getAirlineName(outboundCarrierCode)}</p>
        <h2>귀국 항공편</h2>
        <p><strong>출발 공항:</strong> ${inboundDepartureAirportName}</p>
        <p><strong>도착 공항:</strong> ${inboundArrivalAirportName}</p>
        <p><strong>출발 시간:</strong> ${inboundDepartureTime}</p>
        <p><strong>도착 시간:</strong> ${inboundArrivalTime}</p>
        <p><strong>가격:</strong> ₩${inboundPriceWon}</p>
        <p><strong>항공사:</strong> ${getAirlineName(inboundCarrierCode)}</p>
    `;

    document.getElementById('booking-details').innerHTML = bookingDetails;
}
function proceedToPayment() {
    const params = new URLSearchParams({
        departureAirport: getQueryParam('departureAirport'),
        arrivalAirport: getQueryParam('arrivalAirport'),
        departureTime: getQueryParam('departureTime'),
        arrivalTime: getQueryParam('arrivalTime'),
        price: getQueryParam('price'),
        carrierCode: getQueryParam('carrierCode'),
        returnDepartureAirport: getQueryParam('returnDepartureAirport'),
        returnArrivalAirport: getQueryParam('returnArrivalAirport'),
        returnDepartureTime: getQueryParam('returnDepartureTime'),
        returnArrivalTime: getQueryParam('returnArrivalTime'),
        returnPrice: getQueryParam('returnPrice'),
        returnCarrierCode: getQueryParam('returnCarrierCode')
    });

    window.location.href = `reservation?${params.toString()}`;
}

document.addEventListener('DOMContentLoaded', async () => {
    await loadData();
    displayBookingDetails();

    document.getElementById('go-back-button').addEventListener('click', () => {
        window.history.back();
    });

    document.getElementById('proceed-button').addEventListener('click', proceedToPayment);
});
