import { OrdersAPI, AdminAPI } from '/js/api.js';
import { qs, fmtTime, money, toast } from '/js/utils.js';
import { APP_CONFIG } from '/js/config.js';

const id = location.pathname.split('/').pop();

const $summary = qs('#summary');
const $tbody   = qs('#items tbody');
const $temp = qs('#temp');
const $humid = qs('#humid');
const $ts = qs('#ts');
const $tip = qs('#tip');
const $btnShip = qs('#ship');
const $btnComplete = qs('#complete');

let pollTimer = null;

// ✅ OrderInfoDTO (+ items) 기준
function renderSummary(o) {
  $summary.innerHTML = `
    <div class="kv">
      <div>주문번호</div><div class="v">#${o.orderId}</div>
      <div>주문시간</div><div class="v">${fmtTime(o.orderedAt)}</div>
      <div>상태</div><div class="v" id="st">${o.status}</div>
      <div>합계</div><div class="v">${money(o.totalPrice)}</div>
    </div>
  `;

  // 상태 값은 DTO에 status 필드가 있다고 가정 (String)
  $btnShip.disabled     = (o.status === APP_CONFIG.STATUS.SHIPPING || o.status === APP_CONFIG.STATUS.DONE);
  $btnComplete.disabled = (o.status === APP_CONFIG.STATUS.DONE || o.status === APP_CONFIG.STATUS.PRE);
}

// ✅ 주문 상품 리스트 (OrderItemDTO 가정)
function renderItems(items = []) {
  $tbody.innerHTML = items.map(it => `
    <tr>
      <td>${it.productName || it.productId}</td>
      <td>${it.quantity}</td>
      <td>${money(it.unitPrice)}</td>
    </tr>
  `).join('');
}

// ✅ SensorDataRealtimeDTO 기준
function showTelemetry(d) {
  if (!d) {
    $temp.textContent = '--';
    $humid.textContent = '--';
    $ts.textContent = '--';
    return;
  }
  if (typeof d.temperature === 'number') {
    $temp.textContent = d.temperature.toFixed(1);
  }
  if (typeof d.humidity === 'number') {
    $humid.textContent = d.humidity.toFixed(0);
  }
  if (d.measuredAt) {
    $ts.textContent = fmtTime(d.measuredAt);
  }
}

// 최신 센서값 폴링
async function pollLatest() {
  try {
    // ➜ GET /api/orders/{id}/telemetry/latest → SensorDataRealtimeDTO
    const d = await OrdersAPI.telemetryLatest(id);
    showTelemetry(d);
  } catch (e) {
    // 실패해도 다음 주기에 재시도
  }
}

async function load() {
  // ➜ GET /api/admin/orders/{id} → OrderDetailDTO(혹은 OrderInfoDTO + items)
  const o = await OrdersAPI.get(id);
  renderSummary(o);
  renderItems(o.items || []);

  if (o.status === APP_CONFIG.STATUS.SHIPPING) {
    // 배송 시작 상태: 주기적으로 센서값 폴링
    $tip.textContent = '배송 중 – 최신 센서값을 주기적으로 불러옵니다.';
    pollTimer && clearInterval(pollTimer);
    pollTimer = setInterval(pollLatest, APP_CONFIG.TELEMETRY_POLL_MS);
    await pollLatest();
  } else if (o.status === APP_CONFIG.STATUS.DONE) {
    // 배송 완료 상태: 센서 수집 중단, 최종값(또는 평균값)만 표시
    $tip.textContent = '배송 완료 – 센서 수집이 종료되었습니다.';
    pollTimer && clearInterval(pollTimer);

    // 평균 DTO가 따로 없으면, 서버에서 "마지막 측정값"을 내려줘도 됨
    if (o.telemetryAverage) {
      showTelemetry({
        temperature: o.telemetryAverage.temperature,
        humidity: o.telemetryAverage.humidity,
        measuredAt: o.completedAt || o.updatedAt || o.orderedAt
      });
    } else {
      showTelemetry(null);
    }
  } else {
    // PRE(배송 전)
    $tip.textContent = '배송 전 상태입니다.';
    pollTimer && clearInterval(pollTimer);
    showTelemetry(null);
  }
}

// 배송 시작: 주문 상태를 SHIPPING으로 바꾸고 폴링 시작
$btnShip.addEventListener('click', async () => {
  try {
    // ➜ POST /api/admin/orders/{id}/ship
    await AdminAPI.ship(id);
    toast('배송을 시작했습니다. 실시간 온·습도 수집을 시작합니다.');
    pollTimer && clearInterval(pollTimer);
    await load();   // 상태 다시 불러오면서 SHIPPING이면 폴링 시작
  } catch (e) {
    toast('시작 실패: ' + e.message);
  }
});

// 배송 완료: 상태 DONE, 센서 수집 중단
$btnComplete.addEventListener('click', async () => {
  try {
    // ➜ POST /api/admin/orders/{id}/complete
    await AdminAPI.complete(id);
    toast('배송을 종료하고 센서 수집을 중단했습니다.');
    pollTimer && clearInterval(pollTimer);
    await load();   // DONE 상태로 다시 로딩
  } catch (e) {
    toast('완료 처리 실패: ' + e.message);
  }
});

window.addEventListener('beforeunload', () => {
  pollTimer && clearInterval(pollTimer);
});

load();
