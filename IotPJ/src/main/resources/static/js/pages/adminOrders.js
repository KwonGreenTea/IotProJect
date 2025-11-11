import { AdminAPI } from '/js/api.js';
import { qs, fmtTime, money } from '/js/utils.js';

const $tbody = qs('#tbl tbody');
const $status = qs('#status');
const $q = qs('#q');
const $btn = qs('#search');

async function load(){
  const f = [];
  if($status.value) f.push(`status=${encodeURIComponent($status.value)}`);
  if($q.value)      f.push(`q=${encodeURIComponent($q.value)}`);
  const query = f.length ? `?${f.join('&')}` : '';
  const data = await AdminAPI.list(query);
  const items = data.items || [];
  $tbody.innerHTML = items.map(o => `
    <tr>
      <td>#${o.number ?? o.id}</td>
      <td>${o.customerName ?? '-'}</td>
      <td>${fmtTime(o.createdAt)}</td>
      <td>${o.status}</td>
      <td>${money(o.total)}</td>
      <td><a class="btn" href="/admin/orders/${encodeURIComponent(o.id)}">모니터링</a></td>
    </tr>
  `).join('');
}

$btn.addEventListener('click', load);
$status.addEventListener('change', load);
$q.addEventListener('keydown', e=>{ if(e.key==='Enter') load(); });

load();
