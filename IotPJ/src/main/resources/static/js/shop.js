/*// 공용: 장바구니 로컬스토리지
const CART_KEY = "cart";
const getCart = () => JSON.parse(localStorage.getItem(CART_KEY) || "[]");
const setCart = (c) => localStorage.setItem(CART_KEY, JSON.stringify(c));

// 숫자 포맷
const fmt = (n) => Number(n || 0).toLocaleString();

// ✅ 서버 ProductInfoDTO → 프론트용 객체로 변환
export async function fetchProducts() {
  const res = await fetch("/productList");  
  const data = await res.json(); // ProductInfoDTO[]

  return data.map(p => ({
    productId: p.productId,
    sellerId: p.sellerId,
    productName: p.productName,
    price: Number(p.price ?? 0),
    minTemperature: p.minTemperature,
    maxTemperature: p.maxTemperature,
    minHumidity: p.minHumidity,
    maxHumidity: p.maxHumidity
  }));
}

// ✅ 단일 상품 fetch (상세 페이지용)
export async function fetchProductById(productId) {
  const res = await fetch(`/productList/${productId}`);
  if (!res.ok) throw new Error("상품 상세 조회 실패: " + res.status);
  const p = await res.json();
  return {
    productId: p.productId,
    sellerId: p.sellerId,
    productName: p.productName,
    price: Number(p.price ?? 0),
    minTemperature: p.minTemperature,
    maxTemperature: p.maxTemperature,
    minHumidity: p.minHumidity,
    maxHumidity: p.maxHumidity
  };
}

// ✅ 장바구니 담기
export function addToCart(item) {
  const cart = getCart();
  const idx = cart.findIndex((c) => c.productId === item.productId);
  if (idx >= 0) cart[idx].qty += item.qty || 1;
  else cart.push({
    productId: item.productId,
    productName: item.productName,
    price: item.price,
    qty: item.qty || 1
  });
  setCart(cart);
  alert("장바구니에 담았습니다.");
}

// ✅ 상품 목록 렌더
export function renderList(products, el) {
  el.innerHTML = products.map(p => `
    <div class="card">
      <span class="badge">
        권장 온/습도 T/H: ${p.minTemperature ?? '-'}~${p.maxTemperature ?? '-'}℃ / ${p.minHumidity ?? '-'}~${p.maxHumidity ?? '-'}%
      </span>
      <h3>
        <a href="/catalog/${p.productId}" class="detail-link">${p.productName}</a>
      </h3>
      <div class="price">${fmt(p.price)}원</div>
      <div class="toolbar">
        <button class="btn" data-id="${p.productId}">장바구니</button>
        <a href="/catalog/${p.productId}" class="btn secondary detail-link">상세보기</a>
      </div>
    </div>
  `).join("");

  // 장바구니 버튼 이벤트
  el.querySelectorAll("button[data-id]").forEach(btn => {
    if (btn.classList.contains("detail-link")) return; // 상세보기 버튼 제외
    btn.addEventListener("click", () => {
      const p = products.find(pp => pp.productId === Number(btn.dataset.id));
      if (!p) return;
      addToCart({ 
        productId: p.productId, 
        productName: p.productName, 
        price: p.price, 
        qty: 1 
      });
    });
  });

  // 상세보기 클릭은 브라우저 기본 이동 사용 → JS에서 막지 않음
}

// ✅ 상세 페이지 렌더
export function renderDetail(product, root) {
  root.innerHTML = `
    <div class="card">
      <div class="badge">
        온도 임계치: ${product.minTemperature ?? '-'}~${product.maxTemperature ?? '-'}℃ /
        습도 임계치: ${product.minHumidity ?? '-'}~${product.maxHumidity ?? '-'}%
      </div>
      <h2>${product.productName}</h2>
      <p class="price">${fmt(product.price)}원</p>
      <div class="toolbar">
        <input id="qty" type="number" value="1" min="1"
               style="width:80px;padding:8px;border:1px solid #ddd;border-radius:8px;">
        <button class="btn" id="add">장바구니 담기</button>
        <a class="btn secondary" href="/catalog">목록으로</a>
      </div>
    </div>
  `;

  root.querySelector("#add").onclick = () => {
    const qty = Math.max(1, parseInt(root.querySelector("#qty").value || "1", 10));
    addToCart({ 
      productId: product.productId, 
      productName: product.productName, 
      price: product.price, 
      qty 
    });
  };
}
*/