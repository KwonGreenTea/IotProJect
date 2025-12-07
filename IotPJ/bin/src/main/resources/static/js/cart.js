/*const CART_KEY = "cart";
const getCart = () => JSON.parse(localStorage.getItem(CART_KEY) || "[]");
const setCart = (c) => localStorage.setItem(CART_KEY, JSON.stringify(c));
const fmt = (n) => Number(n || 0).toLocaleString();

export function renderCart(root) {
  let cart = getCart();
  const draw = () => {
    cart = getCart();
    if (cart.length === 0) {
      root.innerHTML = `
        <p>장바구니가 비었습니다.</p>
        <p><a href="/catalog">쇼핑 계속하기 →</a></p>
      `;
      return;
    }

    const rows = cart
      .map(
        (c, i) => `
      <tr>
        <td>${c.name}</td>
        <td>${fmt(c.price)}원</td>
        <td>
          <input
            data-i="${i}"
            type="number"
            min="1"
            value="${c.qty}"
            style="width:80px;padding:6px;border:1px solid #ddd;border-radius:8px;"
          >
        </td>
        <td>${fmt(c.price * c.qty)}원</td>
        <td><button data-rm="${i}" class="btn secondary">삭제</button></td>
      </tr>
    `
      )
      .join("");
    const total = cart.reduce((s, c) => s + c.price * c.qty, 0);

    root.innerHTML = `
      <table class="table">
        <thead>
          <tr>
            <th>상품</th>
            <th>가격</th>
            <th>수량</th>
            <th>합계</th>
            <th></th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>
      <div class="total">총 합계: ${fmt(total)}원</div>
      <div class="toolbar" style="justify-content:flex-end;margin-top:12px;">
        <a class="btn secondary" href="/catalog">계속 쇼핑</a>
        <button class="btn" id="checkout">주문하기</button>
      </div>
    `;

    root.querySelectorAll("input[data-i]").forEach((inp) => {
      inp.onchange = () => {
        const i = +inp.dataset.i;
        const val = Math.max(1, parseInt(inp.value || "1", 10));
        cart[i].qty = val;
        setCart(cart);
        draw();
      };
    });

    root.querySelectorAll("button[data-rm]").forEach((btn) => {
      btn.onclick = () => {
        const i = +btn.dataset.rm;
        cart.splice(i, 1);
        setCart(cart);
        draw();
      };
    });

    // ✅ 여기부터 주문 생성(컨트롤러 만들면 그대로 붙일 부분)
    root.querySelector("#checkout").onclick = async () => {
      if (cart.length === 0) {
        alert("장바구니가 비어 있습니다.");
        return;
      }

      // 나중에 로그인 붙으면 userId는 서버에서 읽도록 바꿀 수 있음
      const userId = prompt("주문자 ID를 입력해주세요.", "demo-user") || "demo-user";

      // OrderInfoDTO는 '요약 정보'라서,
      // 요청 바디에는 userId + items + totalPrice 를 보내고
      // 응답으로 OrderInfoDTO를 받는다고 가정.
      const payload = {
        userId,
        totalPrice: total,
        items: cart.map(c => ({
          productId: c.id,
          name: c.name,
          unitPrice: c.price,
          quantity: c.qty,
        })),
      };

      try {
        // 컨트롤러 만들 때 이 URL과 요청/응답 구조만 맞춰주면 됨.
        const res = await fetch("/api/orders", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });

        if (!res.ok) {
          const text = await res.text();
          console.error("주문 실패 status:", res.status, text);
          alert("주문 처리 중 오류가 발생했습니다.");
          return;
        }

        // 🎯 응답: OrderInfoDTO
        const order = await res.json();
        // order: { orderId, productId, sellerId, userId, deliveryId, orderedAt, totalPrice }

        alert(
          `주문이 접수되었습니다.\n` +
          `주문번호: ${order.orderId ?? "(알 수 없음)"}\n` +
          `주문일시: ${order.orderedAt ?? ""}\n` +
          `총 금액: ${fmt(order.totalPrice ?? total)}원`
        );

        setCart([]);
        draw();
        // 나중에 주문 내역 페이지 만들면 주석 해제
        // location.href = "/orders";
      } catch (err) {
        console.error("주문 요청 오류:", err);
        alert("주문 요청에 실패했습니다. 잠시 후 다시 시도해주세요.");
      }
    };
  };
  draw();
}
*/