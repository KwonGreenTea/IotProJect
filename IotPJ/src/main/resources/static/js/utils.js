export const qs  = (sel, el=document)=>el.querySelector(sel);
export const qsa = (sel, el=document)=>[...el.querySelectorAll(sel)];
export const fmtTime = (ts)=> new Date(ts).toLocaleString();
export const money = (n)=> (n||0).toLocaleString() + '원';

export function toast(msg, ms=2500){
  const t = document.createElement('div');
  t.className = 'toast';
  t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(()=>t.remove(), ms);
}
