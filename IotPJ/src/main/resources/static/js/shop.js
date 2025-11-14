// 공용: 장바구니 로컬스토리지
const CART_KEY = "cart";
const getCart = () => JSON.parse(localStorage.getItem(CART_KEY) || "[]");
const setCart = (c) => localStorage.setItem(CART_KEY, JSON.stringify(c));

// 숫자 포맷
const fmt = (n) => Number(n || 0).toLocaleString();

// ✅ 서버 ProductInfoDTO → 프론트용 객체로 변환
export async function fetchProducts() {
  const res = await fetch("/mock/products");          // 컨트롤러 URL
  const data = await res.json();                      // ProductInfoDTO[]

  return data.map(p => ({
    id: p.productId,
    name: p.productName,
    price: Number(p.price ?? 0),
    tempMin: p.minTemperature,
    tempMax: p.maxTemperature,
    humidMin: p.minHumidity,
    humidMax: p.maxHumidity,
    // description: p.description ?? ""   // 나중에 DTO에 추가되면 이렇게 쓰면 됨
  }));
}

export function addToCart(item) {
  const cart = getCart();
  const idx = cart.findIndex((c) => c.id === item.id);
  if (idx >= 0) cart[idx].qty += item.qty || 1;
  else cart.push({ id: item.id, name: item.name, price: item.price, qty: item.qty || 1 });
  setCart(cart);
  alert("장바구니에 담았습니다.");
}

// ✅ 목록 렌더 (임계값: 최소~최대 범위 표시)
export function renderList(products, el) {
  el.innerHTML = products.map(p => `
    <div class="card">
      <span class="badge">
        임계 T/H:
        ${p.tempMin ?? '-'}~${p.tempMax ?? '-'}℃ /
        ${p.humidMin ?? '-'}~${p.humidMax ?? '-'}%
      </span>
      <h3><a href="/shop/${p.id}">${p.name}</a></h3>
      <div class="price">${fmt(p.price)}원</div>
      <div class="toolbar">
        <button class="btn" data-id="${p.id}">장바구니</button>
        <a class="btn secondary" href="/shop/${p.id}">상세보기</a>
      </div>
    </div>
  `).join("");

  el.querySelectorAll("button[data-id]").forEach(btn => {
    btn.addEventListener("click", () => {
      const p = products.find(pp => pp.id === Number(btn.dataset.id));
      if (!p) return;
      addToCart({ id: p.id, name: p.name, price: p.price, qty: 1 });
    });
  });
}

// ✅ 상세 렌더 (임계값 범위 사용)
export function renderDetail(product, root) {
  root.innerHTML = `
    <div class="card">
      <div class="badge">
        온도 임계치: ${product.tempMin ?? '-'}~${product.tempMax ?? '-'}℃ /
        습도 임계치: ${product.humidMin ?? '-'}~${product.humidMax ?? '-'}%
      </div>
      <h2>${product.name}</h2>
      <p>${product.description || ""}</p>
      <p class="price">${fmt(product.price)}원</p>
      <div class="toolbar">
        <input id="qty" type="number" value="1" min="1"
               style="width:80px;padding:8px;border:1px solid #ddd;border-radius:8px;">
        <button class="btn" id="add">장바구니 담기</button>
        <a class="btn secondary" href="/shop">목록으로</a>
      </div>
    </div>
  `;

  root.querySelector("#add").onclick = () => {
    const qty = Math.max(1, parseInt(root.querySelector("#qty").value || "1", 10));
    addToCart({ id: product.id, name: product.name, price: product.price, qty });
  };
}
