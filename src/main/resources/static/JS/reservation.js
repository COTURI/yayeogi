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

document.addEventListener('DOMContentLoaded', async function() {
    await loadData();

    document.getElementById('bookNowButton').addEventListener('click', async function(e) {
        e.preventDefault(); // 버튼 클릭 시 페이지 새로 고침 방지

        // 사용자의 로그인 상태를 확인하기 위한 API 호출
        const checkLoginResponse = await fetch('/api/check-login');
        const checkLoginResult = await checkLoginResponse.json();

        const userLoggedIn = checkLoginResult.loggedIn; // 로그인 상태 여부

        if (!userLoggedIn) {
            // 로그인 페이지로 리디렉션
            window.location.href = '/paymentlogin';
            return;
        }

        const formData = {
            departureDate: document.getElementById('departureDate').value,
            departureTime: document.getElementById('departureTime').value,
            arrivalTime: document.getElementById('arrivalTime').value,
            returnDate: document.getElementById('returnDate').value,
            returnDepartureTime: document.getElementById('returnDepartureTime').value,
            returnArrivalTime: document.getElementById('returnArrivalTime').value,
            email: document.getElementById('email').value
        };

        try {
            const reservationResponse = await fetch('/reservations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            const reservationResult = await reservationResponse.json();

            console.log('예약 응답:', reservationResult); // 응답 로그

            if (reservationResult.success) {
                alert('예약이 완료되었습니다.');
                window.location.href = '/reservation-confirmation'; // 예약 확인 페이지로 이동
            } else {
                alert('예약 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('예약 중 오류가 발생했습니다.');
        }
    });
});

