document.getElementById('fetchHotels').addEventListener('click', async () => {
    const apiKey = '7pygXkuxGN6Vfo8WoJeRHnpJM8SWZtlP';  // Amadeus API Key
    const apiSecret = 'M9uW6JX4XYjQfu7f';  // Amadeus API Secret
    const hotelId = 'ARPARARA';  // 예시 호텔 ID

    const authResponse = await fetch('https://test.api.amadeus.com/v1/security/oauth2/token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'grant_type': 'client_credentials',
            'M9uW6JX4XYjQfu7f': apiKey,
            'ARPARARA': apiSecret
        })
    });

    const authData = await authResponse.json();
    const accessToken = authData.access_token;

    const hotelsResponse = await fetch(`https://test.api.amadeus.com/v1/reference-data/locations/hotels?hotelIds=${hotelId}`, {
        headers: {
            'Authorization': `Bearer ${accessToken}`
        }
    });

    if (!hotelsResponse.ok) {
        console.error('Failed to fetch hotels:', hotelsResponse.status);
        document.getElementById('result').textContent = 'Failed to fetch hotels.';
        return;
    }

    const hotelsData = await hotelsResponse.json();
    document.getElementById('result').textContent = JSON.stringify(hotelsData, null, 2);
});
