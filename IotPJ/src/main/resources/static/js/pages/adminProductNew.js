// /static/js/pages/adminProductNew.js

(async () => {
  const form = document.querySelector('#productForm');
  const msg = document.querySelector('#msg');

  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (msg) msg.textContent = '';

    const fd = new FormData(form);

    // DTO(ProductInfoDTO)에 맞게 payload 구성
    const payload = {
      productName: fd.get('name')?.trim(),                           // String
      price: Number(fd.get('price') || 0),                           // Long
      minTemperature: fd.get('tempMin') ? Number(fd.get('tempMin')) : null, // Double
      maxTemperature: fd.get('tempMax') ? Number(fd.get('tempMax')) : null, // Double
      minHumidity: fd.get('humidMin') ? Number(fd.get('humidMin')) : null,  // Integer
      maxHumidity: fd.get('humidMax') ? Number(fd.get('humidMax')) : null,  // Integer
      sellerId: "demo-seller"                                       // 임시 값 (필요시 수정)
    };

    // ====== 검증 ======
    if (!payload.productName || payload.price < 0) {
      msg.textContent = '상품명/가격을 확인하세요.';
      return;
    }

    if (payload.minTemperature != null && payload.maxTemperature != null &&
        payload.minTemperature > payload.maxTemperature) {
      msg.textContent = '온도 최소/최대 값을 확인하세요.';
      return;
    }

    if (payload.minHumidity != null && payload.maxHumidity != null &&
        payload.minHumidity > payload.maxHumidity) {
      msg.textContent = '습도 최소/최대 값을 확인하세요.';
      return;
    }

    try {
      // ✅ 서버에 전송 (백엔드 컨트롤러: POST /api/admin/products 에서 ProductInfoDTO 받는다고 가정)
	  await api.post('/api/admin/products', payload);

      msg.textContent = '등록 완료!';
      setTimeout(() => {
        location.href = '/admin/products';   // 등록 후 목록으로 이동
      }, 600);
    } catch (err) {
      console.error(err);
      msg.textContent = '등록 실패. 잠시 후 다시 시도해주세요.';
    }
  });
})();
