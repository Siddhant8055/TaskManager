// Simple frontend JS to interact with backend APIs
const apiBase = '/api'; // backend base path

function $(id){ return document.getElementById(id); }

// UI elements
const showLoginBtn = $('showLoginBtn');
const showRegisterBtn = $('showRegisterBtn');
const loginForm = $('loginForm');
const registerForm = $('registerForm');
const loginBtn = $('loginBtn');
const registerBtn = $('registerBtn');
const authArea = $('authArea');
const userArea = $('userArea');
const usernameDisplay = $('usernameDisplay');
const logoutBtn = $('logoutBtn');
const appArea = $('appArea');
const tasksList = $('tasksList');
const showTaskFormBtn = $('showTaskFormBtn');
const saveTaskBtn = $('saveTaskBtn');
const taskModalEl = document.getElementById('taskModal');
const taskModal = new bootstrap.Modal(taskModalEl);
const searchInput = $('searchInput');
const filterStatus = $('filterStatus');
const filterPriority = $('filterPriority');
const alertArea = $('alertArea');

let editingTaskId = null;
let cachedTasks = [];

function show(el){ el.classList.remove('d-none'); }
function hide(el){ el.classList.add('d-none'); }

function showAlert(message, type='danger', timeout=5000){
  alertArea.innerHTML = `<div class="alert alert-${type} alert-dismissible">${message}<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\"></button></div>`;
  if(timeout>0) setTimeout(()=>{ alertArea.innerHTML = ''; }, timeout);
}

function setAuthUI(loggedIn, username){
  if(loggedIn){
    hide(authArea); show(userArea); show(appArea);
    usernameDisplay.innerText = username || '';
  } else {
    show(authArea); hide(userArea); hide(appArea);
    usernameDisplay.innerText = '';
  }
}

// Auth actions
showLoginBtn.addEventListener('click', ()=>{ show(loginForm); hide(registerForm); });
showRegisterBtn.addEventListener('click', ()=>{ show(registerForm); hide(loginForm); });
logoutBtn.addEventListener('click', ()=>{ localStorage.removeItem('token'); localStorage.removeItem('username'); setAuthUI(false); });

registerBtn.addEventListener('click', async ()=>{
  const username = $('regUsername').value;
  const email = $('regEmail').value;
  const password = $('regPassword').value;
  try{
    const res = await fetch(`${apiBase}/auth/register`, {
      method: 'POST', headers: {'Content-Type':'application/json'},
      body: JSON.stringify({username,email,password})
    });
    if(res.ok){ showAlert('Registered. Please login.', 'success'); hide(registerForm); show(loginForm); }
    else { const txt = await res.text(); showAlert('Register failed: '+txt); }
  } catch(err){ showAlert('Register error: '+err.message); }
});

loginBtn.addEventListener('click', async ()=>{
  const username = $('loginUsername').value;
  const password = $('loginPassword').value;
  try{
    const res = await fetch(`${apiBase}/auth/login`, {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({username,password})
    });
    if(res.ok){
      const data = await res.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', data.username || username);
      setAuthUI(true, data.username || username);
      fetchTasks();
    } else { const txt = await res.text(); showAlert('Login failed: '+txt); }
  } catch(err){ showAlert('Login error: '+err.message); }
});

function authHeaders(){
  const token = localStorage.getItem('token');
  if(!token) return {};
  if(isTokenExpired(token)){
    localStorage.removeItem('token'); localStorage.removeItem('username'); setAuthUI(false);
    showAlert('Session expired. Please login again.','warning');
    return {};
  }
  return { 'Authorization': 'Bearer '+token };
}

function parseJwt(token){
  try{
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g,'+').replace(/_/g,'/'));
    return JSON.parse(decodeURIComponent(escape(decoded)));
  } catch(e){ return null; }
}

function isTokenExpired(token){
  const p = parseJwt(token);
  if(!p || !p.exp) return false;
  // JWT exp is in seconds
  const expMs = p.exp * 1000;
  return Date.now() > expMs;
}

// Tasks
async function fetchTasks(){
  tasksList.innerHTML = '<div class="col-12">Loading...</div>';
  try{
    const res = await fetch(`${apiBase}/tasks`, { headers: {...authHeaders()} });
    if(res.ok){
      const tasks = await res.json(); cachedTasks = tasks; applyFiltersAndRender();
    } else {
      const txt = await res.text(); tasksList.innerHTML = `<div class=\"col-12 text-danger\">Failed to load tasks: ${txt}</div>`;
    }
  } catch(err){ tasksList.innerHTML = `<div class=\"col-12 text-danger\">Error: ${err.message}</div>`; }
}

function applyFiltersAndRender(){
  const q = searchInput.value.trim().toLowerCase();
  const statusF = filterStatus.value;
  const priorityF = filterPriority.value;
  let filtered = cachedTasks.filter(t=>{
    if(q && !(t.title && t.title.toLowerCase().includes(q)) && !(t.description && t.description.toLowerCase().includes(q))) return false;
    if(statusF && t.status !== statusF) return false;
    if(priorityF && t.priority !== priorityF) return false;
    return true;
  });
  renderTasks(filtered);
}

function renderTasks(tasks){
  tasksList.innerHTML = '';
  if(!tasks || tasks.length===0){ tasksList.innerHTML = '<div class="col-12">No tasks yet</div>'; return; }
  tasks.forEach(t=>{
    const div = document.createElement('div');
    div.className = 'col-12 col-md-6';
    const priorityBadge = (p)=>{
      if(!p) return '';
      if(p==='HIGH') return '<span class="badge bg-danger">High</span>';
      if(p==='MEDIUM') return '<span class="badge bg-warning text-dark">Medium</span>';
      return '<span class="badge bg-secondary">Low</span>';
    };
    const statusBadge = (s)=>{
      if(!s) return '';
      if(s==='COMPLETED') return '<span class="badge bg-success">Completed</span>';
      if(s==='IN_PROGRESS') return '<span class="badge bg-primary">In Progress</span>';
      return '<span class="badge bg-secondary">Pending</span>';
    };

    div.innerHTML = `
      <div class="card task-card">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-start">
            <h5 class="card-title mb-1">${t.title}</h5>
            <div>
              ${priorityBadge(t.priority)}
              ${statusBadge(t.status)}
            </div>
          </div>
          <p class="card-text">${t.description || ''}</p>
          <div class="mt-2">
            <button class="btn btn-sm btn-primary me-2" data-id="${t.id}" onclick="editTask(${t.id})">Edit</button>
            <button class="btn btn-sm btn-danger" data-id="${t.id}" onclick="deleteTask(${t.id})">Delete</button>
          </div>
        </div>
      </div>
    `;
    tasksList.appendChild(div);
  });
}

window.editTask = async function(id){
  // load task and open modal for editing
  try{
    const res = await fetch(`${apiBase}/tasks/${id}`, { headers: {...authHeaders()} });
    if(!res.ok) { const txt = await res.text(); showAlert('Failed to load task: '+txt); return; }
    const t = await res.json();
    editingTaskId = id;
    $('taskTitle').value = t.title; $('taskDescription').value = t.description || '';
    $('taskPriority').value = t.priority; $('taskStatus').value = t.status;
    $('taskModalTitle').innerText = 'Edit Task';
    taskModal.show();
  } catch(err){ showAlert('Load error: '+err.message); }
}

window.deleteTask = async function(id){
  if(!confirm('Delete task?')) return;
  try{
    const res = await fetch(`${apiBase}/tasks/${id}`, { method:'DELETE', headers: {...authHeaders()} });
    if(res.ok) fetchTasks(); else { const txt = await res.text(); showAlert('Delete failed: '+txt); }
  } catch(err){ showAlert('Delete error: '+err.message); }
}

showTaskFormBtn.addEventListener('click', ()=>{
  editingTaskId = null;
  $('taskTitle').value = '';
  $('taskDescription').value = '';
  $('taskPriority').value = 'LOW';
  $('taskStatus').value = 'PENDING';
  $('taskModalTitle').innerText = 'Create Task';
  taskModal.show();
});

saveTaskBtn.addEventListener('click', async ()=>{
  const payload = {
    title: $('taskTitle').value,
    description: $('taskDescription').value,
    priority: $('taskPriority').value,
    status: $('taskStatus').value
  };
  try{
    let res;
    if(editingTaskId){
      res = await fetch(`${apiBase}/tasks/${editingTaskId}`, { method:'PUT', headers:{'Content-Type':'application/json', ...authHeaders()}, body: JSON.stringify(payload) });
    } else {
      res = await fetch(`${apiBase}/tasks`, { method:'POST', headers:{'Content-Type':'application/json', ...authHeaders()}, body: JSON.stringify(payload) });
    }
    if(res.ok){ taskModal.hide(); fetchTasks(); }
    else { const txt = await res.text(); showAlert('Save failed: '+txt); }
  } catch(err){ showAlert('Save error: '+err.message); }
});

// Filters
searchInput.addEventListener('input', ()=>{ applyFiltersAndRender(); });
filterStatus.addEventListener('change', ()=>{ applyFiltersAndRender(); });
filterPriority.addEventListener('change', ()=>{ applyFiltersAndRender(); });

// On load check token
document.addEventListener('DOMContentLoaded', ()=>{
  const token = localStorage.getItem('token');
  const username = localStorage.getItem('username');
  if(token){
    if(isTokenExpired(token)){
      localStorage.removeItem('token'); localStorage.removeItem('username');
      setAuthUI(false);
      showAlert('Session expired. Please login again.','warning');
    } else {
      setAuthUI(true, username || '');
      fetchTasks();
      // check periodically for expiry
      setInterval(()=>{
        const t = localStorage.getItem('token'); if(t && isTokenExpired(t)){
          localStorage.removeItem('token'); localStorage.removeItem('username'); setAuthUI(false);
          showAlert('Session expired. Please login again.','warning');
        }
      }, 60*1000);
    }
  } else {
    setAuthUI(false);
  }
});
