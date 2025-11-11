import { OrdersAPI, AdminAPI } from '/js/api.js';
import { qs, fmtTime, money, toast } from '/js/utils.js';
import { APP_CONFIG } from '/js/config.js';

const id = location.pathname.split('/').pop();

const $summary = qs('#summary');
const $tbody   = qs('#items tbody');
const $temp = qs('#temp'); const $humid = qs('#humid'); const $ts = qs('#ts'); const $tip = qs('#tip');
const $btnShip = qs('#ship'); const $btnComplete = qs('#complete');

let pollTimer = null;

function renderSummary(o){
  $summary.innerHTML = `
    <div class="kv">
      <div>주문번호</div><div class="v">#${o.number ?? o.id}</div>
      <div>주문시간</div><div class="v">${fmtTime(o.createdAt)}</div>
      <div>상태</div><div class="v" id="st">${o.status}</div>
      <div>합계</div><div class="v">${money(o.total)}</div>
    </div>`;
  $btnShip.disabled     = (o.status === APP_CONFIG.STATUS.SHIPPING || o.status === APP_CONFIG.STATUS.DONE);
  $btnComplete.disabled = (o.status === APP_CONFIG.STATUS.DONE || o.status === APP_CONFIG.STATUS.PRE);
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
    const d = await OrdersAPI.telemetryLatest(id);
    showTelemetry(d);
  }catch(e){ /* 다음 주기에 재시도 */ }
}

async function load(){
  const o = await OrdersAPI.get(id);
  renderSummary(o);
  renderItems(o.items || []);

  if(o.status === APP_CONFIG.STATUS.SHIPPING){
    $tip.textContent = '배송 중 – 최신 센서값을 주기적으로 불러옵니다.';
    pollTimer = setInterval(pollLatest, APP_CONFIG.TELEMETRY_POLL_MS);
    await pollLatest();
  } else if(o.status === APP_CONFIG.STATUS.DONE){
    $tip.textContent = '배송 완료 – 평균값은 사용자 주문 상세에서 확인됩니다.';
    showTelemetry({ temp:o.telemetryAverage?.temp, humid:o.telemetryAverage?.humid, ts:o.completedAt || o.updatedAt || o.createdAt });
  } else {
    $tip.textContent = '배송 전 상태입니다.';
    showTelemetry(null);
  }
}

$btnShip.addEventListener('click', async ()=>{
  try{
    await AdminAPI.ship(id);
    toast('수집을 시작했습니다.');
    pollTimer && clearInterval(pollTimer);
    await load();
  }catch(e){ toast('시작 실패: ' + e.message); }
});

$btnComplete.addEventListener('click', async ()=>{
  try{
    await AdminAPI.complete(id);
    toast('수집을 중단하고 평균값을 계산했습니다.');
    pollTimer && clearInterval(pollTimer);
    await load();
  }catch(e){ toast('완료 처리 실패: ' + e.message); }
});

window.addEventListener('beforeunload', ()=> pollTimer && clearInterval(pollTimer));
load();
