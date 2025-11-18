const form = document.querySelector('#productForm');
const msg  = document.querySelector('#msg');

form.addEventListener('submit', async (e) => {
  e.preventDefault();

  const formData = new FormData(form);

  const payload = {
    sellerId:       formData.get('sellerId'),      
    productName:    formData.get('productName'),
    price:          Number(formData.get('price')),
    minTemperature: formData.get('minTemperature') ? Number(formData.get('minTemperature')) : null,
    maxTemperature: formData.get('maxTemperature') ? Number(formData.get('maxTemperature')) : null,
    minHumidity:    formData.get('minHumidity') ? Number(formData.get('minHumidity')) : null,
    maxHumidity:    formData.get('maxHumidity') ? Number(formData.get('maxHumidity')) : null,
  };

  try {
    const res = await fetch('/admin/products', {   // ★ 절대 경로
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      msg.textContent = '상품 등록 중 오류가 발생했습니다. 서버 응답이 정상이 아닙니다.';
      return;
    }

    msg.textContent = '상품이 성공적으로 등록되었습니다.';
    // location.href = '/admin/products';  // 필요하면 자동 이동
  } catch (err) {
    console.error(err);
    msg.textContent = '상품 등록 중 네트워크 오류가 발생했습니다.';
  }
});