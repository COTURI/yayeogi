document.getElementById('check-reservation-button').addEventListener('click', function () {

    if (email) {
        // 로그인 상태 확인을 위한 AJAX 요청
        fetch('/api/check-login', {
            method: 'GET',
            credentials: 'include' // 쿠키와 같은 인증 정보를 포함하여 요청
        })
        .then(response => response.json())
        .then(data => {
            if (data.loggedIn) {
                // 로그인된 경우 예약 확인 페이지로 이동
                window.location.href = '/reservation-confirmation?email=' + encodeURIComponent(email);
            } else {
                // 로그인되지 않은 경우 로그인 페이지로 리디렉션
                alert('로그인 후 다시 시도해주세요.');
                window.location.href = '/login';
            }
        })
        .catch(error => {
            console.error('로그인 상태 확인 중 오류 발생:', error);
        });
    } else {
        alert('이메일을 입력해주세요.');
    }
});