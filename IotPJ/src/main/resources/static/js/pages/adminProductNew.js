(async () => {
  const form = document.querySelector('#productForm');
  const msg = document.querySelector('#msg');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    msg.textContent = '';

    const fd = new FormData(form);
    const payload = {
      name: fd.get('name')?.trim(),
      sku: (fd.get('sku')||'').trim() || null,
      price: Number(fd.get('price')||0),
      description: fd.get('description')?.trim() || null,
      tempMin: fd.get('tempMin') ? Number(fd.get('tempMin')) : null,
      tempMax: fd.get('tempMax') ? Number(fd.get('tempMax')) : null,
      humidMin: fd.get('humidMin') ? Number(fd.get('humidMin')) : null,
      humidMax: fd.get('humidMax') ? Number(fd.get('humidMax')) : null,
    };

    // 간단 검증
    if(!payload.name || payload.price < 0){
      msg.textContent = '상품명/가격을 확인하세요.';
      return;
    }
    if(payload.tempMin!=null && payload.tempMax!=null && payload.tempMin>payload.tempMax){
      msg.textContent = '온도 최소/최대 값을 확인하세요.';
      return;
    }
    if(payload.humidMin!=null && payload.humidMax!=null && payload.humidMin>payload.humidMax){
      msg.textContent = '습도 최소/최대 값을 확인하세요.';
      return;
    }

    try{
      await api.post('/api/admin/products', payload);
      msg.textContent = '등록 완료!';
      setTimeout(()=> location.href='/admin/products', 600);
    }catch(err){
      console.error(err);
      msg.textContent = '등록 실패. 잠시 후 다시 시도해주세요.';
    }
  });
})();
