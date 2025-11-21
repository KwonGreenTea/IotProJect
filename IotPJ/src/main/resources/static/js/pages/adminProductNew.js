// /src/main/resources/static/js/pages/adminProductNew.js

window.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("#productForm");
  const msg = document.querySelector("#msg");

  if (!form) return;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    msg.textContent = "";
    msg.className = "form-message";

    const formData = new FormData(form);

    const dto = {
      sellerId: formData.get("sellerId") || "SELLER01", // 셀러ID input 있으면 그대로, 없으면 기본값
      productName: formData.get("productName"),
      price: formData.get("price") ? Number(formData.get("price")) : null,
      minTemperature: formData.get("minTemperature")
        ? Number(formData.get("minTemperature"))
        : null,
      maxTemperature: formData.get("maxTemperature")
        ? Number(formData.get("maxTemperature"))
        : null,
      minHumidity: formData.get("minHumidity")
        ? Number(formData.get("minHumidity"))
        : null,
      maxHumidity: formData.get("maxHumidity")
        ? Number(formData.get("maxHumidity"))
        : null,
    };

    // 필수값 체크 (상품명/가격)
    if (!dto.productName || dto.productName.trim().length === 0) {
      msg.textContent = "상품명을 입력해주세요.";
      msg.classList.add("error");
      return;
    }
    if (dto.price == null || isNaN(dto.price)) {
      msg.textContent = "가격을 올바르게 입력해주세요.";
      msg.classList.add("error");
      return;
    }

    try {
      const res = await fetch("/admin/products", {
        method: "POST",
        headers: {
          "Content-Type": "application/json;charset=UTF-8",
        },
        body: JSON.stringify(dto),
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
