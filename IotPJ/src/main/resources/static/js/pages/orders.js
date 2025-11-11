import { OrdersAPI } from '/js/api.js';
import { qs, fmtTime, money } from '/js/utils.js';

const listEl = qs('#list');

(async ()=>{
  const data = await OrdersAPI.my(); // ?userId=me
  const items = data.items || [];
  if(!items.length){
    listEl.innerHTML = '<div class="small">주문 내역이 없습니다.</div>';
    return;
  }
  listEl.innerHTML = items.map(o => `
    <a href="/orders/${encodeURIComponent(o.id)}">
      <div class="row" style="justify-content:space-between;align-items:center">
        <div>
          <div><b>#${o.number ?? o.id}</b> · <span class="small">${fmtTime(o.createdAt)}</span></div>
          <div class="small">상태: ${o.status}</div>
        </div>
        <div class="badge">${money(o.total)}</div>
      </div>
    </a>
  `).join('');
})();
