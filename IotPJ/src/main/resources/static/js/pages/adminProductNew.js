window.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("#productForm");
  const msg = document.querySelector("#msg");

  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    msg.textContent = "";
    msg.className = "form-message";

    // ★ 폼에서 FormData 그대로 사용 (이미지 포함)
    const formData = new FormData(form);

    const productName = formData.get("productName");
    const priceRaw = formData.get("price");
    const sellerId = formData.get("sellerId") || "SELLER01";

    // 필수값 체크 (상품명/가격)
    if (!productName || productName.trim().length === 0) {
      msg.textContent = "상품명을 입력해주세요.";
      msg.classList.add("error");
      return;
    }
    if (!priceRaw || isNaN(Number(priceRaw))) {
      msg.textContent = "가격을 올바르게 입력해주세요.";
      msg.classList.add("error");
      return;
    }

    // 기본 셀러 ID 세팅 (input이 비어있으면 SELLER01로 대체)
    formData.set("sellerId", sellerId);

    try {
      const res = await fetch("/admin/products", {
        method: "POST",
        // ★ headers 설정하지 말 것! (브라우저가 boundary 포함해서 자동으로 넣어줌)
        body: formData,
      });

      const text = await res.text();

      if (!res.ok || text !== "OK") {
        throw new Error(text || "서버 응답 오류");
      }

      msg.textContent = "상품이 성공적으로 등록되었습니다.";
      msg.classList.add("success");

      // 폼 초기화
      form.reset();
    } catch (err) {
      console.error(err);
      msg.textContent =
        "상품 등록 중 오류가 발생했습니다. 서버 응답이 정상이 아닙니다.";
      msg.classList.add("error");
    }
  });
});