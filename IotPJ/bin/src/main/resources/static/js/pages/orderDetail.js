import { OrdersAPI } from '/js/api.js';
import { qs, fmtTime, money } from '/js/utils.js';
import { APP_CONFIG } from '/js/config.js';

const idFromPath = ()=> location.pathname.split('/').pop();

const $summary = qs('#summary');
const $tbody   = qs('#items tbody');
const $temp = qs('#temp'); const $humid = qs('#humid'); const $ts = qs('#ts'); const $tip = qs('#tip');

let pollTimer = null;

function renderSummary(o){
  $summary.innerHTML = `
    <div class="kv">
      <div>주문번호</div><div class="v">#${o.number ?? o.id}</div>
      <div>주문시간</div><div class="v">${fmtTime(o.createdAt)}</div>
      <div>상태</div><div class="v">${o.status}</div>
      <div>합계</div><div class="v">${money(o.total)}</div>
    </div>`;
}

function renderItems(items=[]){
  $tbody.innerHTML = items.map(it => `
    <tr><td>${it.title || it.sku}</td><td>${it.qty}</td><td>${money(it.price)}</td></tr>
  `).join('');
}

function showTelemetry(d){
  if(!d){ $temp.textContent='--'; $humid.textContent='--'; $ts.textContent='--'; return; }
  if(typeof d.temp==='number')  $temp.textContent = d.temp.toFixed(1);
  if(typeof d.humid==='number') $humid.textContent = d.humid.toFixed(0);
  if(d.ts) $ts.textContent = fmtTime(d.ts);
}

async function pollLatest(){
  try{
    const d = await OrdersAPI.telemetryLatest(idFromPath());
    showTelemetry(d);
  }catch(e){ /* 조용히 재시도 */ }
}

(async ()=>{
  const id = idFromPath();
  const o = await OrdersAPI.get(id);
  renderSummary(o);
  renderItems(o.items || []);

  if(o.status === APP_CONFIG.STATUS.DONE && o.telemetryAverage){
    $tip.textContent = '표시된 값은 배송 완료 시점의 최근 10개 평균입니다.';
    showTelemetry({ temp:o.telemetryAverage.temp, humid:o.telemetryAverage.humid, ts:o.completedAt || o.updatedAt || o.createdAt });
  } else if(o.status === APP_CONFIG.STATUS.SHIPPING) {
    $tip.textContent = '배송 중 – 최신 센서값을 주기적으로 불러옵니다.';
    pollTimer = setInterval(pollLatest, APP_CONFIG.TELEMETRY_POLL_MS);
    await pollLatest();
  } else {
    $tip.textContent = '배송 전 상태입니다.';
    showTelemetry(null);
  }

  window.addEventListener('beforeunload', ()=> pollTimer && clearInterval(pollTimer));
})();
