document.addEventListener('DOMContentLoaded', function () {
    const preparePaymentButton = document.getElementById('preparePaymentButton');
    const approvePaymentButton = document.getElementById('approvePaymentButton');
    const approvalSection = document.getElementById('approvalSection');
    const approvalMessage = document.getElementById('approvalMessage');

    preparePaymentButton.addEventListener('click', function () {
        const orderId = document.getElementById('orderId').value;
        const userId = document.getElementById('userId').value;
        const itemName = document.getElementById('itemName').value;
        const quantity = document.getElementById('quantity').value;
        const totalAmount = document.getElementById('totalAmount').value;
        const taxFreeAmount = document.getElementById('taxFreeAmount').value;

        fetch('/api/payment/prepare', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                orderId: orderId,
                userId: userId,
                itemName: itemName,
                quantity: quantity,
                totalAmount: totalAmount,
                taxFreeAmount: taxFreeAmount
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                throw new Error(data.error);
            }
            approvalSection.style.display = 'block';
            approvalMessage.textContent = `결제 준비가 완료되었습니다. 승인 ID: ${data.approvalId}`;
        })
        .catch(error => {
            console.error('결제 준비 오류:', error);
            alert('결제 준비 중 오류가 발생했습니다: ' + error.message);
        });
    });

    approvePaymentButton.addEventListener('click', function () {
        const pgToken = document.getElementById('pgToken').value;

        fetch('/api/payment/approve', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ pgToken: pgToken })
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                alert('결제가 승인되었습니다.');
            } else {
                throw new Error('결제 승인 실패');
            }
        })
        .catch(error => {
            console.error('결제 승인 오류:', error);
            alert('결제 승인 중 오류가 발생했습니다: ' + error.message);
        });
    });
});
