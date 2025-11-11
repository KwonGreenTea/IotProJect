const CART_KEY = "cart";
const getCart = () => JSON.parse(localStorage.getItem(CART_KEY) || "[]");
const setCart = (c) => localStorage.setItem(CART_KEY, JSON.stringify(c));
const fmt = (n)=> Number(n||0).toLocaleString();

export function renderCart(root){
  let cart = getCart();
  const draw = () => {
    cart = getCart();
    if(cart.length === 0){
      root.innerHTML = `<p>장바구니가 비었습니다.</p><p><a href="/shop">쇼핑 계속하기 →</a></p>`;
      return;
    }
    const rows = cart.map((c,i)=>`
      <tr>
        <td>${c.name}</td>
        <td>${fmt(c.price)}원</td>
        <td>
          <input data-i="${i}" type="number" min="1" value="${c.qty}" style="width:80px;padding:6px;border:1px solid #ddd;border-radius:8px;">
        </td>
        <td>${fmt(c.price * c.qty)}원</td>
        <td><button data-rm="${i}" class="btn secondary">삭제</button></td>
      </tr>
    `).join("");
    const total = cart.reduce((s,c)=> s + (c.price*c.qty), 0);

    root.innerHTML = `
      <table class="table">
        <thead><tr><th>상품</th><th>가격</th><th>수량</th><th>합계</th><th></th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
      <div class="total">총 합계: ${fmt(total)}원</div>
      <div class="toolbar" style="justify-content:flex-end;margin-top:12px;">
        <a class="btn secondary" href="/shop">계속 쇼핑</a>
        <button class="btn" id="checkout">주문하기</button>
      </div>
    `;

    root.querySelectorAll("input[data-i]").forEach(inp=>{
      inp.onchange = ()=>{
        const i = +inp.dataset.i;
        const val = Math.max(1, parseInt(inp.value||"1",10));
        cart[i].qty = val; setCart(cart); draw();
      };
    });
    root.querySelectorAll("button[data-rm]").forEach(btn=>{
      btn.onclick = ()=>{
        const i = +btn.dataset.rm;
        cart.splice(i,1); setCart(cart); draw();
      };
    });

    root.querySelector("#checkout").onclick = async ()=>{
      // ⚠️ 실제 주문 API 연동 시 이 부분만 바꾸면 됨.
      // 예: POST /api/orders  (팀에서 정한 DTO에 맞게 body 구성)
      // 여기서는 데모로 주문 성공 처리만.
      alert("주문이 접수되었습니다. (데모)");
      setCart([]); draw();
      // 주문 후 사용자 주문내역으로 이동하려면: location.href = "/orders";
    };
  };
  draw();
}
