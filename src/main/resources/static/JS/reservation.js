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

    // 쿼리 파라미터 가져오기
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        const value = decodeURIComponentSafe(urlParams.get(param));
        console.log(`Query param [${param}]: ${value}`); // 디버깅용 출력
        return value || '정보 없음';
    }

    // 항공사 데이터 로드
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

    // 예약 상세 정보 표시
 function displayBookingDetails() {
     const departureAirport = getQueryParam('departureAirport');
     const arrivalAirport = getQueryParam('arrivalAirport');
     let departureTime = getQueryParam('departureTime');
     let arrivalTime = getQueryParam('arrivalTime') || '정보 없음';

     // 날짜와 시간을 분리
     const departureDateTime = departureTime ? new Date(departureTime) : null;
     const arrivalDateTime = arrivalTime !== '정보 없음' ? new Date(arrivalTime) : null;

     const departureDate = departureDateTime ? departureDateTime.toISOString().split('T')[0] : '정보 없음';
     const departureTimeOnly = departureDateTime ? departureDateTime.toISOString().split('T')[1].split('.')[0] : '정보 없음'; // HH:MM:SS 형식
     const arrivalDate = arrivalDateTime ? arrivalDateTime.toISOString().split('T')[0] : '정보 없음';
     const arrivalTimeOnly = arrivalDateTime ? arrivalDateTime.toISOString().split('T')[1].split('.')[0] : '정보 없음'; // HH:MM:SS 형식

     // 기존 변수에 시간만 업데이트
     departureTime = departureTimeOnly;
     arrivalTime = arrivalTimeOnly;

     const price = getQueryParam('price');
     const carrierCode = getQueryParam('carrierCode');
     const returnDepartureAirport = getQueryParam('returnDepartureAirport');
     const returnArrivalAirport = getQueryParam('returnArrivalAirport');
     let returnDepartureTime = getQueryParam('returnDepartureTime');
     let returnArrivalTime = getQueryParam('returnArrivalTime') || '정보 없음';

     const returnDepartureDateTime = returnDepartureTime ? new Date(returnDepartureTime) : null;
     const returnArrivalDateTime = returnArrivalTime !== '정보 없음' ? new Date(returnArrivalTime) : null;

     const returnDepartureDate = returnDepartureDateTime ? returnDepartureDateTime.toISOString().split('T')[0] : '정보 없음';
     const returnDepartureTimeOnly = returnDepartureDateTime ? returnDepartureDateTime.toISOString().split('T')[1].split('.')[0] : '정보 없음'; // HH:MM:SS 형식
     const returnArrivalDate = returnArrivalDateTime ? returnArrivalDateTime.toISOString().split('T')[0] : '정보 없음';
     const returnArrivalTimeOnly = returnArrivalDateTime ? returnArrivalDateTime.toISOString().split('T')[1].split('.')[0] : '정보 없음'; // HH:MM:SS 형식

     // 기존 변수에 시간만 업데이트
     returnDepartureTime = returnDepartureTimeOnly;
     returnArrivalTime = returnArrivalTimeOnly;

     const returnPrice = getQueryParam('returnPrice');
     const returnCarrierCode = getQueryParam('returnCarrierCode');

     const outboundPriceWon = convertEuroToWon(parseFloat(price));
     const inboundPriceWon = convertEuroToWon(parseFloat(returnPrice));

     // 예약 정보 표시
     document.getElementById('booking-details').innerHTML = `
         <h2>출발 항공편</h2>
         <p><strong>출발 공항:</strong> ${decodeURIComponentSafe(departureAirport)}</p>
         <p><strong>도착 공항:</strong> ${decodeURIComponentSafe(arrivalAirport)}</p>
         <p><strong>출발 시간:</strong>  ${departureTime}</p>
         <p><strong>도착 시간:</strong> ${arrivalTime}</p>
         <p><strong>출발 날짜:</strong> ${departureDate}</p>
         <p><strong>도착 날짜:</strong> ${arrivalDate}</p>
         <p><strong>가격:</strong> ₩${outboundPriceWon}</p>
         <p><strong>항공사:</strong> ${airlines[carrierCode] || '정보 없음'}</p>
         <h2>귀국 항공편</h2>
         <p><strong>출발 공항:</strong> ${decodeURIComponentSafe(returnDepartureAirport)}</p>
         <p><strong>도착 공항:</strong> ${decodeURIComponentSafe(returnArrivalAirport)}</p>
         <p><strong>출발 시간:</strong>  ${returnDepartureTime}</p>
         <p><strong>도착 시간:</strong>  ${returnArrivalTime}</p>
         <p><strong>출발 날짜:</strong> ${returnDepartureDate}</p>
         <p><strong>도착 날짜:</strong> ${returnArrivalDate}</p>
         <p><strong>가격:</strong> ₩${inboundPriceWon}</p>
         <p><strong>항공사:</strong> ${airlines[returnCarrierCode] || '정보 없음'}</p>
     `;

     // 폼의 숨겨진 입력 필드에 예약 정보 추가
     document.getElementById('departureAirport').value = departureAirport;
     document.getElementById('arrivalAirport').value = arrivalAirport;
     document.getElementById('departureTime').value = departureTime;
     document.getElementById('arrivalTime').value =  arrivalTime;
     document.getElementById('departureDate').value = departureDate;
     document.getElementById('arrivalDate').value = arrivalDate;
     document.getElementById('price').value = price;
     document.getElementById('carrierCode').value = carrierCode;
     document.getElementById('returnDepartureAirport').value = returnDepartureAirport;
     document.getElementById('returnArrivalAirport').value = returnArrivalAirport;
     document.getElementById('returnDepartureTime').value =  returnDepartureTime;
     document.getElementById('returnArrivalTime').value =  returnArrivalTime;
     document.getElementById('returnDepartureDate').value = returnDepartureDate;
     document.getElementById('returnArrivalDate').value = returnArrivalDate;
     document.getElementById('returnPrice').value = returnPrice;
     document.getElementById('returnCarrierCode').value = returnCarrierCode;
 }




    function handleFormSubmit(event) {
        event.preventDefault(); // 기본 폼 제출 동작을 방지합니다
        const form = event.target;
        fetch(form.action, {
            method: form.method,
            body: new FormData(form),
        })
        .then(response => {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                return response.json();
            }
        })
        .then(data => {
            // 응답을 처리합니다
        })
        .catch(error => console.error('Error:', error));
    }



        document.addEventListener('DOMContentLoaded', async () => {


            airlines = await loadAirlines();
            displayBookingDetails();
        });

