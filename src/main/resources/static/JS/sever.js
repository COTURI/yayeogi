const express = require('express');
const fetch = require('node-fetch');
const app = express();
const port = 3000;

// CORS 설정
app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});
  const apiKey = '7pygXkuxGN6Vfo8WoJeRHnpJM8SWZtlP';  // 여기에 실제 API 키를 넣으세요
    const apiSecret = 'M9uW6JX4XYjQfu7f';  // 여기에 실제 API 비밀 키를 넣으세요
    const apiUrl = `https://test.api.amadeus.com/v2/shopping/hotel-offers?cityCode=MIA&checkInDate=2024-08-01&checkOutDate=2024-08-07&adults=1
`;

app.get('/api/hotel-search', async (req, res) => {
    const { cityCode, checkInDate, checkOutDate, adults } = req.query;
    const apiUrl = `https://test.api.amadeus.com/v2/shopping/hotel-offers?cityCode=${cityCode}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&adults=${adults}`;

    try {
        const response = await fetch(apiUrl, {
            headers: {
                'Authorization': 'Bearer YOUR_API_KEY'  // 실제 API 키를 입력
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        res.json(data);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
