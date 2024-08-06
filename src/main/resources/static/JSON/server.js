// server.js (Node.js + Express)

const express = require('express');
const app = express();
const port = 3000;

// 임시 데이터 (DB에서 가져온 것처럼 가정)
const hotelData = {
    name: "호텔 이름",
    latitude: 37.57295,
    longitude: 126.97915
};

app.use(express.static('public')); // 클라이언트 사이드 정적 파일 제공

app.get('/hotel', (req, res) => {
    res.json(hotelData);
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});
