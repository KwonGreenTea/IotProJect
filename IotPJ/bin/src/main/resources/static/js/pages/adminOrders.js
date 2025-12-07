import { AdminAPI } from '/js/api.js';
import { qs, fmtTime, money } from '/js/utils.js';

const $tbody = qs('#tbl tbody');
const $status = qs('#status');
const $q = qs('#q');
const $btn = qs('#search');

async function load() {
  const f = [];
  if ($status.value) f.push(`status=${encodeURIComponent($status.value)}`);
  if ($q.value)      f.push(`q=${encodeURIComponent($q.value)}`);
  const query = f.length ? `?${f.join('&')}` : '';

  // 서버에서 OrderInfoDTO 리스트를 items로 감싸서 내려온다고 가정
  const data = await AdminAPI.list(query);
  const items = data.items || [];

  $tbody.innerHTML = items.map(o => `
    <tr>
      <!-- 주문번호: orderId -->
      <td>#${o.orderId}</td>

      <!-- 고객(주문자): userId 사용 -->
      <td>${o.userId ?? '-'}</td>

      <!-- 주문시간: orderedAt -->
      <td>${o.orderedAt ? fmtTime(o.orderedAt) : '-'}</td>

      <!-- 상태: DTO에 없으니 일단 '-' (나중에 status 필드 생기면 교체) -->
      <td>-</td>

      <!-- 합계: totalPrice -->
      <td>${money(o.totalPrice)}</td>

      <!-- 모니터링 링크도 orderId 기반으로 이동 -->
      <td><a class="btn" href="/admin/orders/${encodeURIComponent(o.orderId)}">모니터링</a></td>
    </tr>
  `).join('');
}

$btn.addEventListener('click', load);
$status.addEventListener('change', load);
$q.addEventListener('keydown', e => { if (e.key === 'Enter') load(); });

load();
