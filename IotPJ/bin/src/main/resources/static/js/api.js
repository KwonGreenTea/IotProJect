const { API_BASE } = window.APP_CONFIG;

async function http(path, opts = {}) {
  const res = await fetch(API_BASE + path, {
    headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
    credentials: 'include',
    ...opts
  });
  if (!res.ok) {
    const msg = await res.text().catch(()=>res.statusText);
    throw new Error(msg || `HTTP ${res.status}`);
  }
  const ct = res.headers.get('content-type') || '';
  return ct.includes('application/json') ? res.json() : res.text();
}

// 사용자
export const OrdersAPI = {
  my: (query='?userId=me') => http(`/orders${query}`),
  get: (id) => http(`/orders/${id}`),
  telemetryLatest: (id) => http(`/orders/${id}/telemetry/latest`)
};

// 운영자
export const AdminAPI = {
  list: (query='') => http(`/admin/orders${query}`),
  ship: (id) => http(`/orders/${id}/ship`, { method:'POST' }),
  complete: (id) => http(`/orders/${id}/complete`, { method:'POST' })
};

export { http };
